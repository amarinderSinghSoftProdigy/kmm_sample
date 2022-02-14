package com.zealsoftsol.medico.core.utils

import com.zealsoftsol.medico.core.mvi.scope.nested.SignUpScope

object Validator {

    object TraderDetails {

        private val PAN_REGEX = Regex("^([a-zA-Z]){5}([0-9]){4}([a-zA-Z]){1}?\$")
        private val GSTIN_REGEX =
            Regex("([0][1-9]|[1-2][0-9]|[3][0-7])([A-Z]{5})([0-9]{4})([A-Z]{1}[1-9A-Z]{1})([Z]{1})([0-9A-Z]{1})+")

        fun isPanValid(pan: String) = PAN_REGEX.matches(pan)

        fun isGstinValid(gstin: String) = GSTIN_REGEX.matches(gstin)

        fun isFoodLicenseValid(foodLicense: Boolean, foodLicenseNumber: String): Boolean {
            return if (foodLicense) {
                foodLicenseNumber.isNotEmpty()
            } else {
                true
            }
        }
    }

    object Aadhaar {

        const val MAX_LENGTH = 12

        private val d by lazy {
            arrayOf(
                intArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
                intArrayOf(1, 2, 3, 4, 0, 6, 7, 8, 9, 5),
                intArrayOf(2, 3, 4, 0, 1, 7, 8, 9, 5, 6),
                intArrayOf(3, 4, 0, 1, 2, 8, 9, 5, 6, 7),
                intArrayOf(4, 0, 1, 2, 3, 9, 5, 6, 7, 8),
                intArrayOf(5, 9, 8, 7, 6, 0, 4, 3, 2, 1),
                intArrayOf(6, 5, 9, 8, 7, 1, 0, 4, 3, 2),
                intArrayOf(7, 6, 5, 9, 8, 2, 1, 0, 4, 3),
                intArrayOf(8, 7, 6, 5, 9, 3, 2, 1, 0, 4),
                intArrayOf(9, 8, 7, 6, 5, 4, 3, 2, 1, 0)
            )
        }
        private val p by lazy {
            arrayOf(
                intArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
                intArrayOf(1, 5, 7, 6, 2, 8, 3, 0, 9, 4),
                intArrayOf(5, 8, 0, 3, 7, 9, 6, 1, 4, 2),
                intArrayOf(8, 9, 1, 6, 0, 4, 3, 5, 2, 7),
                intArrayOf(9, 4, 5, 3, 1, 2, 6, 8, 7, 0),
                intArrayOf(4, 2, 8, 6, 5, 7, 3, 9, 0, 1),
                intArrayOf(2, 7, 9, 3, 8, 0, 6, 4, 1, 5),
                intArrayOf(7, 0, 4, 6, 9, 1, 3, 2, 5, 8)
            )
        }
        private val TRIM_SPACE_REGEX by lazy { "\\s".toRegex() }

        /**
         * Check if aadhaar number is valid or not
         * @param aadhaarNo
         * @return Boolean
         */
        fun isValid(aadhaarNo: String): Boolean {
            // Remove extra spaces
            val clearedAadhaarNo = aadhaarNo.replace(TRIM_SPACE_REGEX, "")
            // Check if aadhaar no has 12 digits
            // Check if aadhaar no consists only of digits
            // Check if aadhaar no first digit is not 0 or 1
            if (clearedAadhaarNo.length != 12 || clearedAadhaarNo.first()
                    .code in 0..1
            ) return false
            // Check using verhoeff algorithm
            var c = 0
            clearedAadhaarNo.map { it.toString().toInt() }.reversed()
                .forEachIndexed { index, value ->
                    c = d[c][p[index % 8][value]]
                }
            return c == 0
        }
    }
}