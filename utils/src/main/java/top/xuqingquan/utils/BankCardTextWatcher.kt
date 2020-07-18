package top.xuqingquan.utils

import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import android.widget.EditText

/**
 * Created by 许清泉 on 2019-07-08 20:31
 * 银行卡效果，4个一空格
 */
class BankCardTextWatcher(private val editText: EditText, maxLength: Int) : TextWatcher {
    //max input length
    private var maxLength =
        DEFAULT_MAX_LENGTH
    private var beforeTextLength = 0
    private var isChanged = false

    //space count
    private var space = 0

    private val buffer = StringBuffer()

    init {
        this.maxLength = maxLength
        editText.addTextChangedListener(this)
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        beforeTextLength = s.length
        if (buffer.isNotEmpty()) {
            buffer.delete(0, buffer.length)
        }
        space = 0
        for (element in s) {
            if (element == ' ') {
                space++
            }
        }
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        val length = s.length
        buffer.append(s.toString())
        if (length == beforeTextLength || length <= 3
            || isChanged
        ) {
            isChanged = false
            return
        }
        isChanged = true
    }

    override fun afterTextChanged(s: Editable) {
        if (isChanged) {
            var selectionIndex = editText.selectionEnd
            //total char length
            var index = 0
            while (index < buffer.length) {
                if (buffer[index] == ' ') {
                    buffer.deleteCharAt(index)
                } else {
                    index++
                }
            }
            //total space count
            index = 0
            var totalSpace = 0
            while (index < buffer.length) {
                if (index == 4 || index == 9 || index == 14 || index == 19 || index == 24) {
                    buffer.insert(index, ' ')
                    totalSpace++
                }
                index++
            }
            //selection index
            if (totalSpace > space) {
                selectionIndex += totalSpace - space
            }
            val tempChar = CharArray(buffer.length)
            buffer.getChars(0, buffer.length, tempChar, 0)
            val str = buffer.toString()
            if (selectionIndex > str.length) {
                selectionIndex = str.length
            } else if (selectionIndex < 0) {
                selectionIndex = 0
            }
            editText.setText(str)
            val text = editText.text
            //set selection
            Selection.setSelection(text, if (selectionIndex < maxLength) selectionIndex else maxLength)
            isChanged = false
        }
    }

    companion object {
        //default max length = 21 + 5 space
        private const val DEFAULT_MAX_LENGTH = 21 + 5

        @JvmStatic
        fun bind(editText: EditText) {
            BankCardTextWatcher(
                editText,
                DEFAULT_MAX_LENGTH
            )
        }

        @JvmStatic
        fun bind(editText: EditText, maxLength: Int) {
            BankCardTextWatcher(editText, maxLength)
        }
    }
}