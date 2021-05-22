package com.github.newscalculator.ui.mainFragment.recyclerView

import android.content.Context
import android.graphics.Rect
import android.util.DisplayMetrics
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * <p>Декоратор для RecyclerView.</p>
 */
class Decoration (private val context: Context) : RecyclerView.ItemDecoration() {
    /**
     * Создание отступов вокруг элемента списка.
     * @param outRect - прямоугольник вокруг элемента
     */
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val offset = 8.fromDpToPixels(context)
        with(outRect) {
            left = offset
            bottom = offset
            right = offset
            top = offset
        }
    }

    /**
     * Переводит dp в пиксели.
     * @return количество пикселей во входном значении dp
     */
    private fun Int.fromDpToPixels (context: Context): Int {
        //плотность экрана пользователя
        val density = context.resources.displayMetrics.densityDpi
        //отношение плотности экрана пользователя к стандартному значению плотности (160 DPI)
        val pixelsInDp = density/ DisplayMetrics.DENSITY_DEFAULT
        return this*pixelsInDp
    }
}