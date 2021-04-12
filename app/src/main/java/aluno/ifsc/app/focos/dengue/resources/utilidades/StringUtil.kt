package aluno.ifsc.app.focos.dengue.resources.utilidades

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

object StringUtil {

    fun checkEmail(aEmail: CharSequence): Boolean {
        val ePattern =
            "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$"
        val p = java.util.regex.Pattern.compile(ePattern)
        val m = p.matcher(aEmail)
        return m.matches()
    }

    fun isCpfValid(CPF: String): Boolean {
        if (CPF.length != 11 ||
            CPF == "00000000000" ||
            CPF == "11111111111" ||
            CPF == "22222222222" ||
            CPF == "33333333333" ||
            CPF == "44444444444" ||
            CPF == "55555555555" ||
            CPF == "66666666666" ||
            CPF == "77777777777" ||
            CPF == "88888888888" ||
            CPF == "99999999999"
        ) {
            return false
        }
        val dig10: Char
        val dig11: Char
        var sm: Int
        var i: Int
        var r: Int
        var num: Int
        var peso: Int
        try {
            sm = 0
            peso = 10
            i = 0
            while (i < 9) {
                num = (CPF[i] - 48).toInt()
                sm += num.times(peso)
                peso -= 1
                i++
            }
            r = 11 - sm % 11
            if (r == 10 || r == 11)
                dig10 = '0'
            else
                dig10 = (r + 48).toChar()
            sm = 0
            peso = 11
            i = 0
            while (i < 10) {
                num = (CPF[i] - 48).toInt()
                sm += num.times(peso)
                peso -= 1
                i++
            }
            r = 11 - sm % 11
            if (r == 10 || r == 11)
                dig11 = '0'
            else
                dig11 = (r + 48).toChar()
            return dig10 == CPF[9] && dig11 == CPF[10]
        } catch (erro: InputMismatchException) {
            return false
        }

    }

    fun isCNPJValid(CNPJ: String): Boolean {
        if (CNPJ == "00000000000000" || CNPJ == "11111111111111" ||
            CNPJ == "22222222222222" || CNPJ == "33333333333333" ||
            CNPJ == "44444444444444" || CNPJ == "55555555555555" ||
            CNPJ == "66666666666666" || CNPJ == "77777777777777" ||
            CNPJ == "88888888888888" || CNPJ == "99999999999999" ||
            CNPJ.length != 14
        )
            return false

        val dig13: Char
        val dig14: Char
        var sm: Int
        var i: Int
        var r: Int
        var num: Int
        var peso: Int

        try {
            sm = 0
            peso = 2
            i = 11
            while (i >= 0) {
                num = CNPJ[i].toInt() - 48
                sm += num * peso
                peso += 1
                if (peso == 10)
                    peso = 2
                i--
            }

            r = sm % 11

            dig13 = if (r == 0 || r == 1)
                '0'
            else
                (11 - r + 48).toChar()

            sm = 0
            peso = 2
            i = 12
            while (i >= 0) {
                num = CNPJ[i].toInt() - 48
                sm += num * peso
                peso += 1
                if (peso == 10)
                    peso = 2
                i--
            }

            r = sm % 11

            dig14 = if (r == 0 || r == 1)
                '0'
            else
                (11 - r + 48).toChar()

            return dig13 == CNPJ[12] && dig14 == CNPJ[13]
        } catch (erro: InputMismatchException) {
            return false
        }

    }

    fun data(aData: Date): String {
        return data("dd/MM/yyyy HH:mm", aData)
    }

    @SuppressLint("SimpleDateFormat")
    fun data(aFormato: String, aDate: String?): String {
        val parse = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(aDate!!)
        return data(aFormato, parse)
    }

    @SuppressLint("SimpleDateFormat")
    fun dataPayment(aFormato: String, aDate: String?): String {
        if(aDate!!.contains("/")) {
            return dataPaymentII(aFormato, aDate)
        }
        val parse = SimpleDateFormat("yyyy-MM-dd").parse(aDate)
        return data(aFormato, parse)
    }

    @SuppressLint("SimpleDateFormat")
    fun dataStringToDate(aDate: String?): Date{
        if(aDate!!.contains("/")) {
            return SimpleDateFormat("dd/MM/yyyy").parse(aDate)!!
        }
        val parse = SimpleDateFormat("yyyy-MM-dd").parse(aDate)
        return parse!!
    }

    @SuppressLint("SimpleDateFormat")
    fun dataPaymentII(aFormato: String, aDate: String?): String {
        val parse = SimpleDateFormat("dd/MM/yyyy").parse(aDate!!)
        return data(aFormato, parse)
    }

    @SuppressLint("SimpleDateFormat")
    fun dataToDate(formato : String, aData: String): Date {
        return SimpleDateFormat(formato).parse(aData)!!
    }

    @SuppressLint("SimpleDateFormat")
    fun data(aFormato: String, aDate: String, aFormatoString : String): String {
        val parse = SimpleDateFormat(aFormatoString).parse(aDate)
        return data(aFormato, parse)
    }

    @SuppressLint("SimpleDateFormat")
    fun data(aData: String): Date {
        return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(aData)!!
    }

    @SuppressLint("SimpleDateFormat")
    fun data(aFormato: String, aData: Date?): String {
        return if (aData != null) SimpleDateFormat(aFormato).format(aData) else ""
    }

    fun data(aData: Date, aFormato: String): String {
        return data(aFormato, aData)
    }

    @SuppressLint("SimpleDateFormat")
    fun dataAtual(aFormato: String): String {
        return SimpleDateFormat(aFormato).format(Date())
    }

    @SuppressLint("SimpleDateFormat")
    fun addDaysAtualDate(aFormato: String, daysAdd : Int): String {
        val calendar : Calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, daysAdd)
        return SimpleDateFormat(aFormato).format(calendar.time)
    }

    fun getLastDayMonth(aCurrentMonth: Calendar): Calendar {
        val lMaxCalendar = aCurrentMonth.clone() as Calendar
        lMaxCalendar.set(
            Calendar.DAY_OF_MONTH,
            aCurrentMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
        )
        lMaxCalendar.set(Calendar.HOUR_OF_DAY, 23)
        lMaxCalendar.set(Calendar.MINUTE, 59)
        lMaxCalendar.set(Calendar.SECOND, 59)
        lMaxCalendar.set(Calendar.MILLISECOND, 999)
        return lMaxCalendar
    }

    fun getFirstDayMonth(aCurrentMonth: Calendar): Calendar {
        val lMinCalendar = aCurrentMonth.clone() as Calendar
        lMinCalendar.set(
            Calendar.DAY_OF_MONTH,
            aCurrentMonth.getActualMinimum(Calendar.DAY_OF_MONTH)
        )
        lMinCalendar.set(Calendar.HOUR_OF_DAY, 0)
        lMinCalendar.set(Calendar.MINUTE, 0)
        lMinCalendar.set(Calendar.SECOND, 0)
        lMinCalendar.set(Calendar.MILLISECOND, 0)
        return lMinCalendar
    }


    fun findDiff(fromDate: Date?, toDate: Date?): Int {
        if (fromDate == null || toDate == null) {
            return -1
        }
        val diff = toDate.time - fromDate.time
        return (diff / (60 * 60 * 1000 * 24 * 30.41666666 * 12)).toInt()
    }
}
