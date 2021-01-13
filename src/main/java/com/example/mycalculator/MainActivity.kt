package com.example.mycalculator

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.pow


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val one = findViewById<Button>(R.id.one)
        val two = findViewById<Button>(R.id.two)
        val three = findViewById<Button>(R.id.three)
        val four = findViewById<Button>(R.id.four)
        val five = findViewById<Button>(R.id.five)
        val six = findViewById<Button>(R.id.six)
        val seven = findViewById<Button>(R.id.seven)
        val eight = findViewById<Button>(R.id.eight)
        val nine = findViewById<Button>(R.id.nine)
        val zero = findViewById<Button>(R.id.zero)
        val point = findViewById<Button>(R.id.point)
        val open = findViewById<Button>(R.id.bracketopen)
        val close = findViewById<Button>(R.id.bracketclosed)
        val back = findViewById<Button>(R.id.cancel)
        var show = ""
        var out = ""
        val output = findViewById<TextView>(R.id.output)
        val input = findViewById<TextView>(R.id.input)
        val plus = findViewById<Button>(R.id.add)
        val minus = findViewById<Button>(R.id.minus)
        val mod = findViewById<Button>(R.id.mod)
        val multi = findViewById<Button>(R.id.multiply)
        val divide = findViewById<Button>(R.id.divide)
        val equal = findViewById<Button>(R.id.equalto)
        val ac = findViewById<Button>(R.id.ac)
        input.text = show
        output.text = out

        fun basic(rightNum:String?, leftNum:String?, op:String?):Double? {
            var f=0
            var newRightNum="0"
            if (rightNum?.length==0){
                f=1
            }
            return when (op) {
                "+" -> {
                    if(f==0){
                        (rightNum?.toDouble()!! + leftNum?.toDouble()!!)
                    }
                    else {
                        (newRightNum?.toDouble()!! + leftNum?.toDouble()!!)
                    }
                }
                "-" -> {
                    if(f==0){
                        (rightNum?.toDouble()!! - leftNum?.toDouble()!!)
                    }
                    else {
                        (newRightNum?.toDouble()!! - leftNum?.toDouble()!!)
                    }
                }
                "*" -> {
                    (rightNum?.toDouble()!! * leftNum?.toDouble()!!)
                }
                "^" -> {
                    ((rightNum?.toDouble()!!).pow(leftNum?.toDouble()!!))
                }
                else -> {
                    (rightNum?.toDouble()!! / leftNum?.toDouble()!!)
                }
            }
        }


        fun elemInside(mainString:String?, listCheck:List<String>):Boolean {
            for (ops in listCheck) {
                if (mainString?.contains(ops)!!){
                    return true
                }
            }
            return false
        }

        fun getOpIndex(query: String?, operations:List<String>):Array<Int> {
            var allIndex:Array<Int> = arrayOf()
            var dupQuery = query
            while (elemInside(dupQuery, operations)) {
                for (op in operations) {
                    if (dupQuery?.contains(op)!!) {
                        allIndex = allIndex.plusElement(dupQuery.indexOf(op))
                        dupQuery = dupQuery.substring(0, dupQuery.indexOf(op)) + '1' + dupQuery.substring(dupQuery.indexOf(op) + 1)
                    }
                }
            }

            allIndex.sort()
            return allIndex
        }


        fun parseSimple(query:String?):Double? {
            val operations = listOf("^", "/", "*", "-", "+")
            var allIndex: Array<Int> = arrayOf()

            var calcQuery = query
            while (elemInside(calcQuery, operations) && (allIndex.size > 1 || if (allIndex.isEmpty()) true else allIndex[0] != 0)) {
                for (op in operations) {
                    calcQuery = calcQuery?.replace("-+", "-")
                    calcQuery = calcQuery?.replace("--", "+")
                    calcQuery = calcQuery?.replace("+-", "-")
                    allIndex = getOpIndex(calcQuery, operations)
                    if (calcQuery?.contains(op)!!) {
                        val indexOp = calcQuery.indexOf(op)
                        val indexIndexOp = allIndex.indexOf(indexOp)
                        val rightIndex =
                                if (indexIndexOp == allIndex.lastIndex) calcQuery.lastIndex else allIndex[indexIndexOp + 1]
                        val leftIndex = if (indexIndexOp == 0) 0 else allIndex[indexIndexOp - 1]
                        val rightNum =
                                calcQuery.slice(if (rightIndex == calcQuery.lastIndex) indexOp + 1..rightIndex else indexOp + 1 until rightIndex)
                        val leftNum = calcQuery.slice(if (leftIndex == 0) leftIndex until indexOp else leftIndex + 1  until indexOp)
                        val result = basic(leftNum, rightNum, op)
                        calcQuery = (if (leftIndex != 0) calcQuery.substring(
                                0,
                                leftIndex + 1
                        ) else "") + result.toString() + (if(rightIndex != calcQuery.lastIndex) calcQuery.substring(
                                rightIndex..calcQuery.lastIndex
                        ) else "")
                    }
                }
            }
            return calcQuery?.toDouble()
        }

        fun getAllIndex(query: String?, char: Char, replacement:String="%"):List<Int> {
            var myQuery = query
            var indexes:List<Int> = listOf()
            while (char in myQuery!!) {
                val indexFinded = myQuery.indexOf(char)
                indexes = indexes.plus(indexFinded)
                myQuery = myQuery.substring(0 until indexFinded) + replacement + myQuery.substring(indexFinded+1..myQuery.lastIndex)
            }
            return indexes
        }

        fun getBrackets(query: String?): List<Int> {
            val allEndIndex = getAllIndex(query, ')')
            val allStartIndex = getAllIndex(query, '(')
            val firstIndex = allStartIndex[0]
            for (endIndex in allEndIndex) {
                val inBrac = query?.substring(firstIndex+1 until endIndex)
                val inBracStart = getAllIndex(inBrac, '(')
                val inBracEnd = getAllIndex(inBrac, ')')
                if (inBracStart.size == inBracEnd.size){
                    return listOf(firstIndex, endIndex)
                }
            }
            return listOf(-1, -1)
        }

        fun evaluate(query:String?):Double? {
            var calcQuery = query
            var index = 0;
            // Check if brackets are present
            while (calcQuery?.contains('(')!! && index < 200){
                val startBrackets = getBrackets(calcQuery)[0]
                val endBrackets = getBrackets(calcQuery)[1]
                val inBrackets = calcQuery!!.slice(startBrackets+1 until endBrackets)
                if ('(' in inBrackets && ')' in inBrackets){
                    val inBracValue = evaluate(inBrackets)
                    calcQuery = calcQuery!!.substring(0, startBrackets) + inBracValue.toString() + (if(endBrackets == calcQuery!!.lastIndex) "" else calcQuery!!.substring(endBrackets+1..calcQuery!!.lastIndex))
                }
                else {
                    val inBracValue = parseSimple(inBrackets)
                    calcQuery = calcQuery!!.substring(0, startBrackets) + inBracValue.toString() + (if(endBrackets == calcQuery!!.lastIndex) "" else calcQuery!!.substring(endBrackets+1..calcQuery!!.lastIndex))
                }
                index++
            }

            return parseSimple(calcQuery)
        }

        one.setOnClickListener {
            show = show +'1'
            input.text=show
        }
        two.setOnClickListener {
            show = show +'2'
            input.text=show
        }
        three.setOnClickListener {
            show = show +'3'
            input.text=show
        }
        four.setOnClickListener {
            show = show +'4'
            input.text=show
        }
        five.setOnClickListener {
            show = show +'5'
            input.text=show
        }
        six.setOnClickListener {
            show = show +'6'
            input.text=show
        }
        seven.setOnClickListener {
            show = show +'7'
            input.text=show
        }
        eight.setOnClickListener {
            show = show +'8'
            input.text=show
        }
        nine.setOnClickListener {
            show = show +'9'
            input.text=show
        }
        zero.setOnClickListener {
            show = show +'0'
            input.text=show
        }
        point.setOnClickListener {
            show = show +'.'
            input.text=show
        }
        open.setOnClickListener {
            show = show +'('
            input.text=show
        }
        close.setOnClickListener {
            show = show +')'
            input.text = show
        }
        back.setOnClickListener {
            show=show.dropLast(1)
            input.text=show
        }
        plus.setOnClickListener {
            show = show +'+'
            input.text=show
        }
        minus.setOnClickListener {
            show = show +'-'
            input.text=show
        }
        multi.setOnClickListener {
            show = show +'*'
            input.text=show
        }
        divide.setOnClickListener {
            show = show +'/'
            input.text=show
        }
        mod.setOnClickListener {
            show = show +'^'
            input.text=show
        }
        equal.setOnClickListener {
            if (show.length!=0) {
                try {
                    out = "  " + evaluate(show).toString()
                    output.text = out
                }
                catch (e: NumberFormatException) {
                    output.text="Syntax Error"
                }
            }
        }
        ac.setOnClickListener {
            show=""
            out=""
            input.text = show
            output.text = out
        }
    }
}