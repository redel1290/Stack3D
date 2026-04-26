package com.stack3d

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Array
import kotlin.math.abs
import kotlin.math.min

class Stack3DGame : ApplicationAdapter() {
    private lateinit var batch: SpriteBatch
    private lateinit var shapeRenderer: ShapeRenderer
    private lateinit var font: BitmapFont
    private lateinit var bigFont: BitmapFont
    private lateinit var camera: OrthographicCamera
    
    private val blocks = Array<Block>()
    private val debris = Array<Debris>()
    private val particles = Array<Particle>()
    
    private var currentBlock: Block? = null
    private var viewY = 0f
    private var speed = 3.5f
    private var dir = 1f
    private var gameOver = false
    private var isMoving = false
    private var score = 0
    private var combo = 0
    private var perfectStreak = 0
    private var bestScore = 0
    private var hue = 200f
    private var moveAxis = 'x'
    private var towerShake = 0f
    
    private val zoom = 0.6f
    private val blockHeight = 35f
    
    private var floatingTexts = Array<FloatingText>()
    private var showStartHint = true
    
    override fun create() {
        batch = SpriteBatch()
        shapeRenderer = ShapeRenderer()
        shapeRenderer.setAutoShapeType(true)
        
        font = BitmapFont().apply {
            data.setScale(1.2f)
        }
        bigFont = BitmapFont().apply {
            data.setScale(4f)
        }
        
        camera = OrthographicCamera()
        camera.setToOrtho(false, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        
        // Завантажуємо рекорд
        val prefs = Gdx.app.getPreferences("Stack3DPrefs")
        bestScore = prefs.getInteger("bestScore", 0)
        
        // Обробка дотиків
        Gdx.input.inputProcessor = object : InputAdapter() {
            override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
                if (gameOver) {
                    init()
                } else {
                    drop()
                }
                return true
            }
        }
        
        init()
    }
    
    private fun init() {
        blocks.clear()
        debris.clear()
        particles.clear()
        floatingTexts.clear()
        
        score = 0
        combo = 0
        perfectStreak = 0
        speed = 3.5f
        gameOver = false
        isMoving = false
        moveAxis = 'x'
        towerShake = 0f
        viewY = 0f
        showStartHint = true
        
        hue = MathUtils.random(360f)
        
        blocks.add(Block(0f, 0f, 0f, 180f, 180f, blockHeight, hue))
        spawnBlock()
    }
    
    private fun spawnBlock() {
        val last = blocks.peek()
        moveAxis = if (moveAxis == 'x') 'z' else 'x'
        
        currentBlock = Block(
            x = last.x,
            z = last.z,
            y = last.y - blockHeight,
            w = last.w,
            d = last.d,
            h = blockHeight,
            color = hue.also { hue = (hue + 7f) % 360f }
        )
        
        if (moveAxis == 'x') {
            currentBlock!!.x = -320f
            dir = 1f
        } else {
            currentBlock!!.z = -320f
            dir = 1f
        }
    }
    
    private fun drop() {
        if (gameOver) return
        
        if (!isMoving) {
            isMoving = true
            showStartHint = false
            return
        }
        
        val current = currentBlock ?: return
        val last = blocks.peek()
        
        val delta = if (moveAxis == 'x') current.x - last.x else current.z - last.z
        val size = if (moveAxis == 'x') last.w else last.d
        val overlap = size - abs(delta)
        
        if (overlap <= 0) {
            handleGameOver()
            return
        }
        
        if (abs(delta) < 8) {
            // PERFECT!
            combo++
            perfectStreak++
            score += combo
            towerShake = 6f
            createParticles(current.x, current.z, current.color)
            
            if (perfectStreak >= 5) {
                current.w += 15f
                current.d += 15f
                perfectStreak = 0
                showFloatingText("EXPANDED!", Color.valueOf("00ff88"), 0)
            } else {
                showFloatingText("PERFECT!", Color.valueOf("00f2fe"), combo)
            }
            
            if (moveAxis == 'x') current.x = last.x else current.z = last.z
        } else {
            // GOOD
            val cutSize = abs(delta)
            perfectStreak = 0
            combo = 0
            score += 1
            
            if (moveAxis == 'x') {
                val debrisX = if (delta > 0) current.x + overlap else current.x - cutSize
                createDebris(debrisX, current.y, current.z, cutSize, current.d, current.color)
                current.w = overlap
                if (delta < 0) current.x = last.x
            } else {
                val debrisZ = if (delta > 0) current.z + overlap else current.z - cutSize
                createDebris(current.x, current.y, debrisZ, current.w, cutSize, current.color)
                current.d = overlap
                if (delta < 0) current.z = last.z
            }
            
            showFloatingText("GOOD!", Color.WHITE, 1)
        }
        
        blocks.add(current)
        speed = min(8.5f, 3.5f + blocks.size * 0.05f)
        spawnBlock()
    }
    
    private fun createDebris(x: Float, y: Float, z: Float, w: Float, d: Float, color: Float) {
        debris.add(Debris(x, y, z, w, d, color))
    }
    
    private fun createParticles(x: Float, z: Float, color: Float) {
        repeat(12) {
            particles.add(
                Particle(
                    x = x,
                    z = z,
                    y = currentBlock!!.y,
                    vx = (MathUtils.random() - 0.5f) * 12f,
                    vz = (MathUtils.random() - 0.5f) * 12f,
                    vy = -MathUtils.random() * 6f,
                    color = color,
                    size = MathUtils.random() * 3f + 2f
                )
            )
        }
    }
    
    private fun handleGameOver() {
        gameOver = true
        isMoving = false
        
        if (score > bestScore) {
            bestScore = score
            val prefs = Gdx.app.getPreferences("Stack3DPrefs")
            prefs.putInteger("bestScore", bestScore)
            prefs.flush()
        }
    }
    
    private fun showFloatingText(text: String, color: Color, points: Int) {
        floatingTexts.add(FloatingText(text, color, points))
    }
    
    private fun update(delta: Float) {
        if (gameOver || !isMoving) return
        
        currentBlock?.let { current ->
            if (moveAxis == 'x') {
                current.x += speed * dir
                if (abs(current.x) > 320) dir *= -1
            } else {
                current.z += speed * dir
                if (abs(current.z) > 320) dir *= -1
            }
        }
        
        if (towerShake > 0) {
            towerShake *= 0.85f
            if (towerShake < 0.1f) towerShake = 0f
        }
        
        // Оновлення уламків
        val debrisIter = debris.iterator()
        while (debrisIter.hasNext()) {
            val d = debrisIter.next()
            d.y += d.vy
            d.vy += d.ay
            d.life--
            if (d.life <= 0) debrisIter.remove()
        }
        
        // Оновлення частинок
        val particleIter = particles.iterator()
        while (particleIter.hasNext()) {
            val p = particleIter.next()
            p.x += p.vx
            p.z += p.vz
            p.y += p.vy
            p.vy += 0.25f
            p.life -= 0.025f
            if (p.life <= 0) particleIter.remove()
        }
        
        // Оновлення летючих текстів
        val textIter = floatingTexts.iterator()
        while (textIter.hasNext()) {
            val t = textIter.next()
            t.time += delta
            if (t.time > 0.8f) textIter.remove()
        }
        
        // Камера слідує за вежею
        val targetViewY = blocks.size * blockHeight * zoom
        viewY += (targetViewY - viewY) * 0.1f
    }
    
    private fun drawCube(x: Float, y: Float, z: Float, w: Float, d: Float, h: Float, colorHue: Float, isMovingBlock: Boolean) {
        var sX = 0f
        var sY = 0f
        
        if (!isMovingBlock && towerShake > 0) {
            sX = (MathUtils.random() - 0.5f) * towerShake
            sY = (MathUtils.random() - 0.5f) * towerShake
        }
        
        val isoX = Gdx.graphics.width / 2f + (x - z) * zoom + sX
        val isoY = Gdx.graphics.height / 2f + (x + z) / 2f * zoom + y * zoom + viewY + sY
        val sw = w * zoom
        val sd = d * zoom
        val sh = h * zoom
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        
        // Top face
        shapeRenderer.color = hslToRgb(colorHue, 0.7f, 0.6f)
        shapeRenderer.triangle(
            isoX, isoY,
            isoX + sw, isoY + sw / 2,
            isoX + sw - sd, isoY + (sw + sd) / 2
        )
        shapeRenderer.triangle(
            isoX, isoY,
            isoX + sw - sd, isoY + (sw + sd) / 2,
            isoX - sd, isoY + sd / 2
        )
        
        // Right side (темніша)
        shapeRenderer.color = hslToRgb(colorHue, 0.7f, 0.45f)
        val rx1 = isoX + sw
        val ry1 = isoY + sw / 2
        val rx2 = isoX + sw
        val ry2 = isoY + sw / 2 + sh
        val rx3 = isoX + sw - sd
        val ry3 = isoY + (sw + sd) / 2 + sh
        val rx4 = isoX + sw - sd
        val ry4 = isoY + (sw + sd) / 2
        
        shapeRenderer.triangle(rx1, ry1, rx2, ry2, rx3, ry3)
        shapeRenderer.triangle(rx1, ry1, rx3, ry3, rx4, ry4)
        
        // Left side (найтемніша)
        shapeRenderer.color = hslToRgb(colorHue, 0.7f, 0.35f)
        val lx1 = isoX - sd
        val ly1 = isoY + sd / 2
        val lx2 = isoX - sd
        val ly2 = isoY + sd / 2 + sh
        val lx3 = isoX + sw - sd
        val ly3 = isoY + (sw + sd) / 2 + sh
        val lx4 = isoX + sw - sd
        val ly4 = isoY + (sw + sd) / 2
        
        shapeRenderer.triangle(lx1, ly1, lx2, ly2, lx3, ly3)
        shapeRenderer.triangle(lx1, ly1, lx3, ly3, lx4, ly4)
        
        shapeRenderer.end()
    }
    
    private fun hslToRgb(h: Float, s: Float, l: Float): Color {
        val c = (1f - abs(2f * l - 1f)) * s
        val x = c * (1f - abs(((h / 60f) % 2f) - 1f))
        val m = l - c / 2f
        
        val (r1, g1, b1) = when {
            h < 60 -> Triple(c, x, 0f)
            h < 120 -> Triple(x, c, 0f)
            h < 180 -> Triple(0f, c, x)
            h < 240 -> Triple(0f, x, c)
            h < 300 -> Triple(x, 0f, c)
            else -> Triple(c, 0f, x)
        }
        
        return Color(r1 + m, g1 + m, b1 + m, 1f)
    }
    
    override fun render() {
        val delta = Gdx.graphics.deltaTime
        update(delta)
        
        Gdx.gl.glClearColor(0.02f, 0.02f, 0.04f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        
        camera.update()
        shapeRenderer.projectionMatrix = camera.combined
        batch.projectionMatrix = camera.combined
        
        // Малюємо блоки
        for (block in blocks) {
            drawCube(block.x, block.y, block.z, block.w, block.d, block.h, block.color, false)
        }
        
        // Малюємо уламки
        for (d in debris) {
            drawCube(d.x, d.y, d.z, d.w, d.d, blockHeight, d.color, false)
        }
        
        // Малюємо частинки
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        for (p in particles) {
            val px = Gdx.graphics.width / 2f + (p.x - p.z) * zoom
            val py = Gdx.graphics.height / 2f + (p.x + p.z) / 2f * zoom + p.y * zoom + viewY
            shapeRenderer.color = hslToRgb(p.color, 1f, 0.7f).apply { a = p.life }
            shapeRenderer.rect(px, py, p.size, p.size)
        }
        shapeRenderer.end()
        
        // Малюємо поточний блок
        currentBlock?.let {
            if (!gameOver) {
                drawCube(it.x, it.y, it.z, it.w, it.d, it.h, it.color, true)
            }
        }
        
        // UI
        batch.begin()
        
        // Рахунок
        bigFont.color = Color.valueOf("00f2fe")
        val scoreText = score.toString()
        val scoreLayout = bigFont.draw(batch, scoreText, 
            Gdx.graphics.width / 2f - bigFont.getRegion().regionWidth / 2f, 
            Gdx.graphics.height - 100f)
        
        // Комбо
        if (combo > 1) {
            font.color = Color.valueOf("ffeb3b")
            font.draw(batch, "COMBO ×$combo", 
                Gdx.graphics.width / 2f - 80f, 
                Gdx.graphics.height - 200f)
        }
        
        // Рекорд
        font.color = Color.valueOf("00f2fe")
        font.draw(batch, "РЕКОРД: $bestScore", 
            Gdx.graphics.width / 2f - 100f, 
            Gdx.graphics.height - 250f)
        
        // Летючі тексти
        for (ft in floatingTexts) {
            val alpha = 1f - (ft.time / 0.8f)
            val y = Gdx.graphics.height / 2f - ft.time * 150f
            bigFont.color = ft.color.cpy().apply { a = alpha }
            val text = if (ft.points > 0) "${ft.text}\n+${ft.points}" else ft.text
            bigFont.draw(batch, text, Gdx.graphics.width / 2f - 150f, y)
        }
        
        // Підказка на початку
        if (showStartHint) {
            font.color = Color.valueOf("00f2fe")
            font.draw(batch, "ТОРКНІТЬСЯ, ЩОБ ПОЧАТИ", 
                Gdx.graphics.width / 2f - 200f, 
                200f)
        }
        
        // Game Over
        if (gameOver) {
            bigFont.color = Color.valueOf("ff3e3e")
            bigFont.draw(batch, "ГРА ЗАКІНЧЕНА", 
                Gdx.graphics.width / 2f - 250f, 
                Gdx.graphics.height / 2f + 100f)
            
            font.color = Color.WHITE
            font.draw(batch, "РАХУНОК: $score", 
                Gdx.graphics.width / 2f - 100f, 
                Gdx.graphics.height / 2f)
            
            if (score == bestScore && score > 0) {
                font.color = Color.valueOf("00f2fe")
                font.draw(batch, "НОВИЙ РЕКОРД!", 
                    Gdx.graphics.width / 2f - 120f, 
                    Gdx.graphics.height / 2f - 50f)
            }
            
            font.color = Color.valueOf("00f2fe")
            font.draw(batch, "ТОРКНІТЬСЯ ДЛЯ ПЕРЕЗАПУСКУ", 
                Gdx.graphics.width / 2f - 200f, 
                Gdx.graphics.height / 2f - 150f)
        }
        
        batch.end()
    }
    
    override fun dispose() {
        batch.dispose()
        shapeRenderer.dispose()
        font.dispose()
        bigFont.dispose()
    }
}

data class Block(
    var x: Float,
    var z: Float,
    var y: Float,
    var w: Float,
    var d: Float,
    val h: Float,
    val color: Float
)

data class Debris(
    var x: Float,
    var y: Float,
    var z: Float,
    var w: Float,
    var d: Float,
    val color: Float,
    var vy: Float = 0f,
    var ay: Float = 0.6f,
    var life: Int = 90
)

data class Particle(
    var x: Float,
    var z: Float,
    var y: Float,
    var vx: Float,
    var vz: Float,
    var vy: Float,
    val color: Float,
    val size: Float,
    var life: Float = 1f
)

data class FloatingText(
    val text: String,
    val color: Color,
    val points: Int,
    var time: Float = 0f
)
