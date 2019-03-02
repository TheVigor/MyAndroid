package com.noble.activity.myandroid.models

class CpuInfo {
    var usage: Int = 0
        private set
    private var mLastTotal: Long = 0
    private var mLastIdle: Long = 0

    init {
        usage = 0
        mLastTotal = 0
        mLastIdle = 0
    }

    fun update(parts: Array<String>) {
        // the columns are:
        //
        // 0 "cpu": the string "cpu" that identifies the line
        // 1 user: normal processes executing in user mode
        // 2 nice: nice processes executing in user mode
        // 3 system: processes executing in kernel mode
        // 4 idle: twiddling thumbs
        // 5 io_wait: waiting for I/O to complete
        // 6 irq: servicing interrupts
        // 7 soft_irq: servicing soft_irq
        //
        val idle = java.lang.Long.parseLong(parts[4], 10)
        var total: Long = 0
        var head = true
        for (part in parts) {
            if (head) {
                head = false
                continue
            }
            total += java.lang.Long.parseLong(part, 10)
        }
        val diffIdle = idle - mLastIdle
        val diffTotal = total - mLastTotal
        usage = ((diffTotal - diffIdle).toFloat() / diffTotal * 100).toInt()
        mLastTotal = total
        mLastIdle = idle
    }
}