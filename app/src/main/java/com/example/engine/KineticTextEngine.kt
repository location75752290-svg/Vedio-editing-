package com.example.engine

import androidx.compose.ui.geometry.Offset

/**
 * Parameters for Kinetic Typography Simulation
 */
data class KineticParams(
    val gravity: Float = 9.8f,
    val restitution: Float = 0.75f,
    val springTension: Float = 180f,
    val damping: Float = 0.65f
)

/**
 * Physics state representation for individual letters / characters
 */
class LetterPhysics(val char: Char) {
    var posX: Float = 0f
    var posY: Float = 0f
    var velX: Float = 0f
    var velY: Float = 0f
    var forceX: Float = 0f
    var forceY: Float = 0f
    var restitution: Float = 0.75f
    var mass: Float = 1.0f

    fun applyForce(fx: Float, fy: Float) {
        forceX += fx
        forceY += fy
    }

    fun applySpringForce(tension: Float, targetX: Float = 0f, targetY: Float = 0f) {
        val dx = targetX - posX
        val dy = targetY - posY
        forceX += dx * tension
        forceY += dy * tension
    }

    fun step(dt: Float = 0.016f) {
        velX += (forceX / mass) * dt
        velY += (forceY / mass) * dt
        posX += velX * dt
        posY += velY * dt
        // Reset accumulators
        forceX = 0f
        forceY = 0f
    }
}

object KineticTextEngine {

    fun simulateMomentumCollision(letters: List<LetterPhysics>) {
        if (letters.isEmpty()) return
        // Newton's Cradle momentum transfer collision physics
        val firstLetter = letters.first()
        firstLetter.velX = 180f
        firstLetter.velY = 0f
        
        for (i in 0 until letters.size - 1) {
            val current = letters[i]
            val next = letters[i + 1]
            // Elastic collision momentum conservation transfer with restitution
            val impulse = current.velX * current.restitution
            next.velX += impulse * 0.85f
            current.velX *= (1f - current.restitution)
        }
    }

    fun applyKineticText(text: String, preset: String, params: KineticParams): List<LetterPhysics> {
        val letters = text.map { LetterPhysics(it) }
        when (preset.uppercase()) {
            "GRAVITY" -> letters.forEach {
                it.applyForce(0f, params.gravity * 50f)
                it.restitution = params.restitution
            }
            "SPRING" -> letters.forEach {
                it.applySpringForce(tension = params.springTension)
            }
            "CRADLE" -> simulateMomentumCollision(letters) // Pehla word takraye, baqi hilain
        }
        return letters
    }
}

// Global top-level functions matching user requested API signature
fun applyKineticText(text: String, preset: String, params: KineticParams): List<LetterPhysics> {
    return KineticTextEngine.applyKineticText(text, preset, params)
}

fun simulateMomentumCollision(letters: List<LetterPhysics>) {
    KineticTextEngine.simulateMomentumCollision(letters)
}
