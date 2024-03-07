package com.reco1l.legacy.ui

import org.andengine.engine.camera.hud.HUD
import org.andengine.entity.IEntity
import org.andengine.entity.modifier.AlphaModifier
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener
import org.andengine.entity.primitive.Rectangle
import org.andengine.input.touch.TouchEvent
import org.andengine.util.color.Color
import org.andengine.util.modifier.IModifier
import org.andengine.util.modifier.ease.EaseLinear
import org.andengine.util.modifier.ease.IEaseFunction
import ru.nsu.ccfit.zuev.osu.GlobalManager
import ru.nsu.ccfit.zuev.osu.game.cursor.main.CursorEntity

object MainOverlay : HUD()
{

    private var cursorSprites = arrayOf<CursorEntity>()


    override fun onManagedUpdate(pSecondsElapsed: Float)
    {
        cursorSprites.forEach { it.update(pSecondsElapsed) }

        super.onManagedUpdate(pSecondsElapsed)
    }


    fun onSkinChanged()
    {
        cursorSprites.forEach(CursorEntity::detachSelf)

        // This will load possible new textures from the skin.
        cursorSprites = Array(10) {

            CursorEntity().also { it.attachToScene(this) }
        }
    }


    override fun onSceneTouchEvent(event: TouchEvent): Boolean
    {
        if (GlobalManager.getInstance().engine.scene != GlobalManager.getInstance().gameScene.scene)
        {
            val id = event.pointerID

            if (id < 0 || id > cursorSprites.size)
                return false

            cursorSprites[id].apply {

                x = event.x
                y = event.y

                setShowing(event.isActionDown || event.isActionMove)
            }
        }

        // Do not intercept touch events.
        return super.onSceneTouchEvent(event)
    }


    @JvmStatic
    @JvmOverloads
    fun startTransition(
        duration: Float,
        initialAlpha: Float,
        finalAlpha: Float,
        easing: IEaseFunction? = null,
        onEnd: Runnable? = null
    )
    {
        val dimRectangle = Rectangle(0f, 0f, camera.width, camera.height, GlobalManager.getInstance().engine.vertexBufferObjectManager)
        dimRectangle.color = Color.BLACK
        dimRectangle.alpha = initialAlpha

        dimRectangle.registerEntityModifier(AlphaModifier(duration, initialAlpha, finalAlpha, object : IEntityModifierListener {

            override fun onModifierStarted(pModifier: IModifier<IEntity>, pItem: IEntity) = Unit

            override fun onModifierFinished(pModifier: IModifier<IEntity>, pItem: IEntity)
            {
                onEnd?.run()
                postRunnable(pItem::detachSelf)
            }

        }, easing ?: EaseLinear.getInstance()))

        attachChild(dimRectangle)
    }
}