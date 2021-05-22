package com.github.newscalculator.ui.mainFragment

import com.github.newscalculator.domain.entities.AbstractDiseaseType

/**
 * Интерфейс для связи.
 *
 * Связь главного фрагмента с диалогом редактирования измеренных показателей больного.
 * @property allowToCallDialog Флаг, указывающий на то, можно ли запускать этот диалог или нет. До его введения в 4 версии
 * при быстром нажатии на элемент списка выбрасывалось исключение [java.lang.IllegalArgumentException] и приложение падало.
 */
interface ConnectionToDialog {
    var allowToCallDialog: Boolean

    /**
     * Метод для передачи информации из диалога в вызывающий фрагмент.
     *
     * @param diseaseParameter тип параметра [com.github.newscalculator.domain.entities.AbstractDiseaseType], который был отредактирован.
     * @param measuredValue измеренное числовое значение.
     * @param measuredIsChecked измеренное булево значение.
     */
    fun onDialogClicked(
        diseaseParameter: AbstractDiseaseType,
        measuredValue: Double,
        measuredIsChecked: Boolean = false
    )
}