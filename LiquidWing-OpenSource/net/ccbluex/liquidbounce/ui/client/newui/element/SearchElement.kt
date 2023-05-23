package net.ccbluex.liquidbounce.ui.client.newui.element

import ad.utils.Color.modules.CustomUI
import net.ccbluex.liquidbounce.ui.client.newui.ColorManager
import net.ccbluex.liquidbounce.ui.client.newui.IconManager
import net.ccbluex.liquidbounce.ui.client.newui.extensions.animSmooth
import net.ccbluex.liquidbounce.ui.cnfont.FontLoaders
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.MouseUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.utils.render.Stencil
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.util.List
import kotlin.math.abs

class SearchElement(val xPos: Float, val yPos: Float, val width: Float, val height: Float) {

    private var scrollHeight = 0F
    private var animScrollHeight = 0F
    private var lastHeight = 0F

    private val searchBox = SearchBox(0, xPos.toInt() + 2, yPos.toInt() + 2, width.toInt() - 4, height.toInt() - 2)

    fun drawBox(mouseX: Int, mouseY: Int, accentColor: Color): Boolean {
        RenderUtils.drawRoundedRect(xPos - 0.5F, yPos - 0.5F, xPos + width + 0.5F, yPos + height + 0.5F, 4F, ColorManager.buttonOutline.rgb)
        Stencil.write(true)
        RenderUtils.drawRoundedRect(xPos, yPos, xPos + width, yPos + height, 4F, ColorManager.textBox.rgb)
        Stencil.erase(true)
        if (searchBox.isFocused()) {
            RenderUtils.drawRect(xPos, yPos + height - 1F, xPos + width, yPos + height, accentColor.rgb)
            searchBox.drawTextBox()
        } else if (searchBox.text.length <= 0) {
            searchBox.text = "Search"
            searchBox.drawTextBox()
            searchBox.text = ""
        } else
            searchBox.drawTextBox()

        Stencil.dispose()
        GlStateManager.disableAlpha()
        RenderUtils.drawImage(IconManager.search, (xPos + width - 15F).toInt(), (yPos + 5F).toInt(), 10, 10)
        GlStateManager.enableAlpha()
        return searchBox.text.length > 0
    }

    fun drawPanel(mX: Int, mY: Int, x: Float, y: Float, w: Float, h: Float, wheel: Int, ces: List<CategoryElement>, accentColor: Color) {
        var mouseX = mX
        var mouseY = mY
        lastHeight = 0F
        if (CustomUI.Chinese.get()) {
            for (ce in ces) {
                for (me in ce.moduleElements) {
                    if (me.module.Chinese.startsWith(searchBox.text, true))
                        lastHeight += me.animHeight + 40F
                }
            }
        } else
            for (ce in ces) {
                for (me in ce.moduleElements) {
                    if (me.module.name.startsWith(searchBox.text, true))
                        lastHeight += me.animHeight + 40F
                }
            }
        if (lastHeight >= 10F) lastHeight -= 10F
        handleScrolling(wheel, h)
        drawScroll(x, y + 50F, w, h)
        if (CustomUI.Chinese.get()){
            FontLoaders.F18.drawString("搜索", x + 10F, y + 10F, -1)
            FontLoaders.F10.drawString("搜索", x - 170F, y - 12F, -1)
        }
        else {
            Fonts.misans35.drawString("Search", x + 10F, y + 10F, -1)
            Fonts.font20.drawString("Search", x - 170F, y - 12F, -1)
        }
        RenderUtils.drawImage(IconManager.back, (x - 190F).toInt(), (y - 15F).toInt(), 10, 10)
        var startY = y + 50F
        if (mouseY < y + 50F || mouseY >= y + h)
            mouseY = -1
        RenderUtils.makeScissorBox(x, y + 50F, x + w, y + h)
        GL11.glEnable(3089)
        if (CustomUI.Chinese.get()) {
            for (ce in ces) {
                for (me in ce.moduleElements) {
                    if (me.module.Chinese.startsWith(searchBox.text, true)) {
                        if (startY + animScrollHeight > y + h || startY + animScrollHeight + 40F + me.animHeight < y + 50F)
                            startY += 40F + me.animHeight
                        else
                            startY += me.drawElement(mouseX, mouseY, x, startY + animScrollHeight, w, 40F, accentColor)
                    }
                }
            }
            GL11.glDisable(3089)
        }
        else
            for (ce in ces) {
                for (me in ce.moduleElements) {
                    if (me.module.name.startsWith(searchBox.text, true)) {
                        if (startY + animScrollHeight > y + h || startY + animScrollHeight + 40F + me.animHeight < y + 50F)
                            startY += 40F + me.animHeight
                        else
                            startY += me.drawElement(mouseX, mouseY, x, startY + animScrollHeight, w, 40F, accentColor)
                    }
                }
            }
        GL11.glDisable(3089)
    }


    private fun handleScrolling(wheel: Int, height: Float) {
        if (wheel != 0) {
            if (wheel > 0)
                scrollHeight += 50F
            else
                scrollHeight -= 50F
        }
        if (lastHeight > height - 60F)
            scrollHeight = scrollHeight.coerceIn(-lastHeight + height - 60, 0F)
        else
            scrollHeight = 0F
        animScrollHeight = animScrollHeight.animSmooth(scrollHeight, 0.5F)
    }

    private fun drawScroll(x: Float, y: Float, width: Float, height: Float) {
        if (lastHeight > height - 60F) {
            val last = (height - 60F) - (height - 60F) * ((height - 60F) / lastHeight)
            val multiply = last * abs(animScrollHeight / (-lastHeight + height - 60)).coerceIn(0F, 1F)
            RenderUtils.drawRoundedRect(x + width - 6F, y + 5F + multiply, x + width - 4F, y + 5F + (height - 60F) * ((height - 60F) / lastHeight) + multiply, 1F, 1358954495L.toInt())
        }
    }

    fun handleMouseClick(mX: Int, mY: Int, mouseButton: Int, x: Float, y: Float, w: Float, h: Float, ces: List<CategoryElement>) {
        if (MouseUtils.mouseWithinBounds(mX, mY, x - 200F, y - 20F, x - 170F, y)) {
            searchBox.text = ""
            return
        }
        var mouseX = mX
        var mouseY = mY
        searchBox.mouseClicked(mouseX, mouseY, mouseButton)
        if (searchBox.text.length <= 0) return
        if (mouseY < y + 50F || mouseY >= y + h)
            mouseY = -1
        var startY = y + 50F
        if (CustomUI.Chinese.get()) {
            for (ce in ces)
                for (me in ce.moduleElements)
                    if (me.module.Chinese.startsWith(searchBox.text, true)) {
                        me.handleClick(mouseX, mouseY, x, startY + animScrollHeight, w, 40F)
                        startY += 40F + me.animHeight
                    }
        }
        else
            for (ce in ces)
                for (me in ce.moduleElements)
                    if (me.module.name.startsWith(searchBox.text, true)) {
                        me.handleClick(mouseX, mouseY, x, startY + animScrollHeight, w, 40F)
                        startY += 40F + me.animHeight
                    }
    }

    fun handleMouseRelease(mX: Int, mY: Int, mouseButton: Int, x: Float, y: Float, w: Float, h: Float, ces: List<CategoryElement>) {
        var mouseX = mX
        var mouseY = mY
        if (searchBox.text.length <= 0) return
        if (mouseY < y + 50F || mouseY >= y + h)
            mouseY = -1
        var startY = y + 50F
        if (CustomUI.Chinese.get()) {
            for (ce in ces)
                for (me in ce.moduleElements)
                    if (me.module.Chinese.startsWith(searchBox.text, true)) {
                        me.handleRelease(mouseX, mouseY, x, startY + animScrollHeight, w, 40F)
                        startY += 40F + me.animHeight
                    }
        }
        else
            for (ce in ces)
                for (me in ce.moduleElements)
                    if (me.module.name.startsWith(searchBox.text, true)) {
                        me.handleRelease(mouseX, mouseY, x, startY + animScrollHeight, w, 40F)
                        startY += 40F + me.animHeight
                    }
    }

    fun handleTyping(typedChar: Char, keyCode: Int, x: Float, y: Float, w: Float, h: Float, ces: List<CategoryElement>): Boolean {
        searchBox.textboxKeyTyped(typedChar, keyCode)
        if (searchBox.text.length <= 0) return false
        if (CustomUI.Chinese.get()) {
            for (ce in ces)
                for (me in ce.moduleElements)
                    if (me.module.Chinese.startsWith(searchBox.text, true))
                        if (me.handleKeyTyped(typedChar, keyCode))
                            return true
            return false
        }
        else
            for (ce in ces)
                for (me in ce.moduleElements)
                    if (me.module.name.startsWith(searchBox.text, true))
                        if (me.handleKeyTyped(typedChar, keyCode))
                            return true
        return false
    }

    fun isTyping(): Boolean = (searchBox.text.length > 0)

}