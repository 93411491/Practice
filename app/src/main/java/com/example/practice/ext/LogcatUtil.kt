package com.example.practice.ext

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

/**
 *  adb logcat 日志工具类，
 *  startLogcat :启动 adb logcat 输出日志到本地文件
 *  stopLogcat ： 停止读取日志，并释放资源
 *  getLogcatLogs ： 获取日志文件中的内容
 *  deleteLogFile ： 删除旧的本地日志文件
 */
object LogcatUtil {

    private const val TAG = "LogcatUtil"

    private const val LOG_FILE_NAME = "android_log_file.txt"

    private const val MAX_LOG_FILE_SIZE = 2 * 1024 * 1024  //日志文件最大的大小为2M

    //正在读取状态标记位
    private var isReading = AtomicBoolean(false)

    private lateinit var applicationContext: Context

    private val currentFileSize = AtomicLong(0L)

    private var logFileBufferWriter: BufferedWriter? = null

    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private var logJob: Job? = null

    private val logFile: File by lazy {
        createLogFile()
    }

    private var logProcess: Process? = null
    private var logReader: BufferedReader? = null

    @JvmStatic
    fun init(context: Context) {
        this.applicationContext = context.applicationContext
    }

    //写入一行日志到文件
    @Synchronized
    private fun writeLine(line: String) {
        "尝试写入文件，line = $line".logd(TAG)
        try {
            if (currentFileSize.get() > MAX_LOG_FILE_SIZE) {
                logFileBufferWriter?.close()
                logFileBufferWriter = BufferedWriter(FileWriter(logFile, false))
                currentFileSize.set(0L)

                val header = """
                    文件存储内容超出最大值${MAX_LOG_FILE_SIZE / 1024 / 1024} M，重新记录内容: ${
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(
                        Date()
                    )
                }
                """.trimIndent()
                logFileBufferWriter?.write(header)
                currentFileSize.set(header.length.toLong())
            }
            logFileBufferWriter?.apply {
                write(line)
                newLine()
                flush()
                currentFileSize.addAndGet(line.length.toLong() + 1)
            }
            "写入文件成功".logd(TAG)
        } catch (e: Exception) {
            "写入文件失败，e = $e".logE(TAG)
        }
    }

    //启动 adb logcat 输出日志到本地文件
    @Synchronized
    fun startLogcat(filter: String? = null): Boolean {

        if (isReading.get()) {
            "正在读取中，无法重复开始".logW(TAG)
            return false
        }

        return try {
            cleanOldLogContent()
            setUpAdbLogResource(filter)
            logJob = coroutineScope.launch {
                try {
                    isReading.set(true)
                    // 写入日志头部信息
                    val header = """
                        ===================================
                        日志开始记录: ${
                        SimpleDateFormat(
                            "yyyy-MM-dd HH:mm:ss",
                            Locale.getDefault()
                        ).format(Date())
                    }
                        ===================================
                        
                    """.trimIndent()

                    writeLine(header)
                    while (isActive && isReading.get()) {
                        val line = logReader?.readLine()

                        if (line?.contains(TAG) == true) {
                            continue
                        }

                        if (line.isNullOrEmpty()) {
                            delay(100)
                        } else {
                            writeLine(line)
                        }
                    }
                } catch (e: Exception) {
                    "读取logcat日志失败,e = $e".logE(TAG)
                }
            }
            "开始读取日志成功".logd(TAG)
            true
        } catch (e: Exception) {
            "开始读取日志失败,e = $e".logE(TAG)
            false
        }
    }

    private fun setUpAdbLogResource(filter: String?) {
        //设置写入器，覆盖写入
        logFileBufferWriter = BufferedWriter(FileWriter(logFile, false))
        currentFileSize.set(0L)

        //获取应用id
        val currentPid = android.os.Process.myPid()

        val command = if (!filter.isNullOrEmpty()) {
            arrayOf("logcat", "-v", "time", "--pid=$currentPid", "-s", filter)
        } else {
            arrayOf("logcat", "-v", "time", "--pid=$currentPid")
        }
        "command = $command".logd(TAG)

        logProcess = Runtime.getRuntime().exec(command)
        //日志读取器
        logReader = BufferedReader(InputStreamReader(logProcess?.inputStream))
    }

    //停止读取日志，并释放资源
    @Synchronized
    fun stopLogcat() {
        "尝试停止读取日志".logd(TAG)
        if (!isReading.get()) {
            "已经停止，不需要再次停止".logW(TAG)
            return
        }

        coroutineScope.launch {
            try {
                "销毁进程，终止输入".logd(TAG)
                isReading.set(false)

                logProcess?.destroy()
                logProcess?.waitFor()
                logProcess = null

                "销毁协程，终止读取数据".logd(TAG)
                logJob?.cancelAndJoin()


                "关闭读取器".logd(TAG)
                logReader?.close()
                logReader = null

                "关闭写入器".logd(TAG)
                logFileBufferWriter?.close()
                logFileBufferWriter = null

                "停止读取日志成功".logd(TAG)
            } catch (e: Exception) {
                "停止读取日志失败".logd(TAG)
            }
        }
    }

    //获取日志文件中的内容
    @Synchronized
    fun getLogcatLogs(): String {
        "尝试获取日志".logd(TAG)
        return try {
            logFile.bufferedReader().use {
                it.readLines().joinToString("\n").also { content ->
                    content.logd(TAG)
                }
            }
        } catch (e: Exception) {
            "获取日志失败，e = $e".logE(TAG)
            ""
        }
    }

    /**
     * 创建保存日志的文件
     */
    private fun createLogFile(): File =
        File(applicationContext.getExternalFilesDir(null), LOG_FILE_NAME)

    // 删除旧的日志内容
    @Synchronized
    private fun cleanOldLogContent() {
        Runtime.getRuntime().exec(arrayOf("logcat", "-c")).waitFor().also {
            if (it == 0) {
                "清除成功".logI(TAG)
            } else {
                "清除失败".logW(TAG)
            }
        }
        deleteLogFile()
    }

    //删除旧的本地日志文件
    @Synchronized
    fun deleteLogFile(): Boolean {
        return try {
            // 先关闭所有资源
            stopLogcat()

            // 检查文件是否存在
            if (logFile.exists()) {
                val deleted = logFile.delete()
                if (deleted) {
                    "日志文件删除成功".logd(TAG)
                } else {
                    "日志文件删除失败".logW(TAG)
                }
                deleted
            } else {
                "日志文件不存在，无需删除".logd(TAG)
                true
            }
        } catch (e: Exception) {
            "删除日志文件失败,e = $e".logE(TAG)
            false
        }
    }
}