package model

import java.time.LocalDate

open class Buff(
        val name:String,
        val duration: LocalDate = LocalDate.now().plusDays(1)
)