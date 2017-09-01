package uno.gln

import com.jogamp.opengl.GL2ES2.GL_TEXTURE_COMPARE_MODE
import glm_.set
import glm_.vec2.Vec2i
import org.lwjgl.opengl.*
import org.lwjgl.opengl.GL11.*
import org.lwjgl.system.MemoryUtil
import java.nio.ByteBuffer
import java.nio.IntBuffer

/**
 * Created by elect on 18/04/17.
 */


fun glBindTexture(target: Int, texture: IntBuffer) = glBindTexture(target, texture[0])

fun glTexStorage2D(target: Int, internalFormat: Int, size: Vec2i) = GL42.glTexStorage2D(target, 1, internalFormat, size.x, size.y)

fun glBindTexture(target: Int) = glBindTexture(target, 0)

inline fun withTexture1d(texture: Int, block: Texture1d.() -> Unit) {
    glBindTexture(GL_TEXTURE_1D, texture)
    Texture1d.block()
    glBindTexture(GL_TEXTURE_1D, 0)
}

inline fun withTexture2d(texture: Int, block: Texture2d.() -> Unit) {
    glBindTexture(GL_TEXTURE_2D, texture)
    Texture2d.block()
    glBindTexture(GL_TEXTURE_2D, 0)
}

inline fun withTexture(target: Int, texture: Int, block: Texture.() -> Unit) {
    Texture.target = target
    glBindTexture(target, texture)
    Texture.block()
    glBindTexture(target, 0)
}

inline fun withTexture1d(unit: Int, texture: Int, sampler: IntBuffer, block: Texture1d.() -> Unit) {
    GL13.glActiveTexture(GL13.GL_TEXTURE0 + unit)
    Texture1d.name = texture  // bind
    GL33.glBindSampler(unit, sampler[0])
    Texture1d.block()
    glBindTexture(GL_TEXTURE_1D, 0)
    GL33.glBindSampler(0, sampler[0])
}

inline fun withTexture2d(unit: Int, texture: Int, sampler: IntBuffer, block: Texture2d.() -> Unit) =
        withTexture2d(unit, texture, sampler[0], block)

inline fun withTexture2d(unit: Int, texture: Int, sampler: Int, block: Texture2d.() -> Unit) {
    GL13.glActiveTexture(GL13.GL_TEXTURE0 + unit)
    Texture2d.name = texture  // bind
    GL33.glBindSampler(unit, sampler)
    Texture2d.block()
    glBindTexture(GL_TEXTURE_2D, 0)
    GL33.glBindSampler(0, sampler)
}

inline fun withTexture2d(unit: Int, texture: Int, block: Texture2d.() -> Unit) {
    GL13.glActiveTexture(GL13.GL_TEXTURE0 + unit)
    Texture2d.name = texture  // bind
    Texture2d.block()
    glBindTexture(GL_TEXTURE_2D, 0)
}

inline fun withTexture(unit: Int, target: Int, texture: Int, sampler: Int, block: Texture.() -> Unit) {
    Texture.target = target
    GL13.glActiveTexture(GL13.GL_TEXTURE0 + unit)
    Texture.name = texture  // bind
    GL33.glBindSampler(unit, sampler)
    Texture.block()
    glBindTexture(target, 0)
    GL33.glBindSampler(0, sampler)
}

inline fun initTexture1d(texture: IntBuffer, block: Texture1d.() -> Unit) {
    texture[0] = initTexture1d(block)
}

inline fun initTexture1d(block: Texture1d.() -> Unit): Int {
    val name = glGenTextures()
    Texture1d.name = name   // bind
    Texture1d.block()
    return name
}

inline fun initTexture2d(texture: IntBuffer, block: Texture2d.() -> Unit) {
    texture[0] = initTexture2d(block)
}

inline fun initTexture2d(block: Texture2d.() -> Unit): Int {
    val name = glGenTextures()
    Texture2d.name = name   // bind
    Texture2d.block()
    return name
}

inline fun initTexture(target: Int, texture: IntBuffer, block: Texture.() -> Unit) {
    texture[0] = initTexture(target, block)
}

inline fun initTexture(target: Int, block: Texture.() -> Unit): Int {
    val name = glGenTextures()
    Texture.target = target
    Texture.name = name   // bind
    Texture.block()
    return name
}


inline fun initTextures2d(textures: IntBuffer, block: Textures2d.() -> Unit) {
    glGenTextures(textures)
    Textures2d.names = textures
    Textures2d.block()
}

inline fun initTextures(target: Int, textures: IntBuffer, block: Textures.() -> Unit) {
    glGenTextures(textures)
    Textures.target = target
    Textures.names = textures
    Textures.block()
}

object Texture {

    var target = 0
    var name = 0
        set(value) {
            glBindTexture(target, value)
            field = name
        }

    fun image1d(internalFormat: Int, width: Int, format: Int, type: Int, pixels: ByteBuffer) =
            GL11.glTexImage1D(GL_TEXTURE_1D, 0, internalFormat, width, 0, format, type, pixels)

    fun image1d(level: Int, internalFormat: Int, width: Int, format: Int, type: Int, pixels: ByteBuffer) =
            GL11.glTexImage1D(GL_TEXTURE_1D, level, internalFormat, width, 0, format, type, pixels)

    var baseLevel = 0
        set(value) {
            glTexParameteri(target, GL12.GL_TEXTURE_BASE_LEVEL, value)
            field = value
        }
    var maxLevel = 1_000
        set(value) {
            glTexParameteri(target, GL12.GL_TEXTURE_MAX_LEVEL, value)
            field = value
        }
    var levels = 0..1_000
        set(value) {
            glTexParameteri(target, GL12.GL_TEXTURE_BASE_LEVEL, value.first)
            glTexParameteri(target, GL12.GL_TEXTURE_MAX_LEVEL, value.endInclusive)
            field = value
        }
}

object Texture1d {

    var name = 0
        set(value) {
            glBindTexture(GL_TEXTURE_1D, value)
            field = name
        }

    fun image(internalFormat: Int, width: Int, format: Int, type: Int, pixels: ByteBuffer) =
            GL11.glTexImage1D(GL_TEXTURE_1D, 0, internalFormat, width, 0, format, type, pixels)

    fun image(level: Int, internalFormat: Int, width: Int, format: Int, type: Int, pixels: ByteBuffer) =
            GL11.glTexImage1D(GL_TEXTURE_1D, level, internalFormat, width, 0, format, type, pixels)

    var baseLevel = 0
        set(value) {
            glTexParameteri(GL_TEXTURE_1D, GL12.GL_TEXTURE_BASE_LEVEL, value)
            field = value
        }
    var maxLevel = 1_000
        set(value) {
            glTexParameteri(GL_TEXTURE_1D, GL12.GL_TEXTURE_MAX_LEVEL, value)
            field = value
        }
    var levels = 0..1_000
        set(value) {
            glTexParameteri(GL_TEXTURE_1D, GL12.GL_TEXTURE_BASE_LEVEL, value.first)
            glTexParameteri(GL_TEXTURE_1D, GL12.GL_TEXTURE_MAX_LEVEL, value.endInclusive)
            field = value
        }
}

object Texture2d {

    var name = 0
        set(value) {
            glBindTexture(GL_TEXTURE_2D, value)
            field = name
        }

    fun image(internalFormat: Int, width: Int, height: Int, format: Int, type: Int) =
            GL11.glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width, height, 0, format, type, MemoryUtil.NULL)

    fun image(level: Int, internalFormat: Int, width: Int, height: Int, format: Int, type: Int) =
            GL11.glTexImage2D(GL_TEXTURE_2D, level, internalFormat, width, height, 0, format, type, MemoryUtil.NULL)

    fun image(internalFormat: Int, width: Int, height: Int, format: Int, type: Int, pixels: ByteBuffer) =
            GL11.glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width, height, 0, format, type, pixels)

    fun image(level: Int, internalFormat: Int, width: Int, height: Int, format: Int, type: Int, pixels: ByteBuffer) =
            GL11.glTexImage2D(GL_TEXTURE_2D, level, internalFormat, width, height, 0, format, type, pixels)

    // TODO size for others
    fun image(internalFormat: Int, size: Vec2i, format: Int, type: Int) =
            GL11.glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, size.x, size.y, 0, format, type, MemoryUtil.NULL)

    fun image(level: Int, internalFormat: Int, size: Vec2i, format: Int, type: Int) =
            GL11.glTexImage2D(GL_TEXTURE_2D, level, internalFormat, size.x, size.y, 0, format, type, MemoryUtil.NULL)

    fun image(internalFormat: Int, size: Vec2i, format: Int, type: Int, pixels: ByteBuffer) =
            GL11.glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, size.x, size.y, 0, format, type, pixels)

    fun image(level: Int, internalFormat: Int, size: Vec2i, format: Int, type: Int, pixels: ByteBuffer) =
            GL11.glTexImage2D(GL_TEXTURE_2D, level, internalFormat, size.x, size.y, 0, format, type, pixels)

    fun storage(internalFormat: Int, size: Vec2i) = storage(1, internalFormat, size)
    fun storage(levels: Int, internalFormat: Int, size: Vec2i) =
            GL42.glTexStorage2D(GL_TEXTURE_2D, levels, internalFormat, size.x, size.y)

    var baseLevel = 0
        set(value) {
            glTexParameteri(GL_TEXTURE_2D, GL12.GL_TEXTURE_BASE_LEVEL, value)
            field = value
        }
    var maxLevel = 1_000
        set(value) {
            glTexParameteri(GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, value)
            field = value
        }
    var levels = 0..1_000
        set(value) {
            glTexParameteri(GL_TEXTURE_1D, GL12.GL_TEXTURE_BASE_LEVEL, value.first)
            glTexParameteri(GL_TEXTURE_1D, GL12.GL_TEXTURE_MAX_LEVEL, value.endInclusive)
            field = value
        }

    fun levels(base: Int = 0, max: Int = 1_000) {
        glTexParameteri(GL_TEXTURE_2D, GL12.GL_TEXTURE_BASE_LEVEL, base)
        glTexParameteri(GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, max)
    }

    val linear = Filter.linear
    val nearest = Filter.nearest

    val nearest_mmNearest = Filter.nearest_mmNearest
    val linear_mmNearest = Filter.linear_mmNearest
    val nearest_mmLinear = Filter.nearest_mmLinear
    val linear_mmLinear = Filter.linear_mmLinear

    val clampToEdge = Wrap.clampToEdge
    val mirroredRepeat = Wrap.mirroredRepeat
    val repeat = Wrap.repeat

    var magFilter = linear
        set(value) {
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, value.i)
            field = value
        }
    var minFilter = nearest_mmLinear
        set(value) {
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, value.i)
            field = value
        }

    fun filter(min: Filter = nearest_mmLinear, mag: Filter = linear) {
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, min.i)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, mag.i)
    }

    enum class Filter(val i: Int) { nearest(GL_NEAREST), linear(GL_LINEAR),
        nearest_mmNearest(GL_NEAREST_MIPMAP_NEAREST), linear_mmNearest(GL_LINEAR_MIPMAP_NEAREST),
        nearest_mmLinear(GL_NEAREST_MIPMAP_LINEAR), linear_mmLinear(GL_LINEAR_MIPMAP_LINEAR)
    }

    //    var maxAnisotropy = 1.0f
//        set(value) {
//            glTexParameteri(name, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, value)
//            field = value
//        }
    var wrapS = repeat
        set(value) {
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, value.i)
            field = value
        }
    var wrapT = repeat
        set(value) {
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, value.i)
            field = value
        }

    fun wrap(s: Wrap = repeat, t: Wrap = repeat) {
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, s.i)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, t.i)
    }

    enum class Wrap(val i: Int) { clampToEdge(GL12.GL_CLAMP_TO_EDGE), mirroredRepeat(GL14.GL_MIRRORED_REPEAT), repeat(GL_REPEAT) }


    val rToTexture = CompareMode.rToTexture
    val none = CompareMode.none
    val lessEqual = CompareFunc.lessEqual
    val greaterEqual = CompareFunc.greaterEqual
    val less = CompareFunc.less
    val greater = CompareFunc.greater
    val equal = CompareFunc.equal
    val notEqual = CompareFunc.notEqual
    val always = CompareFunc.always
    val never = CompareFunc.never

    var compareFunc = rToTexture
        set(value) = glTexParameteri(GL_TEXTURE_2D, GL14.GL_TEXTURE_COMPARE_FUNC, value.i)
    var compareMode = lessEqual
        set(value) = glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_MODE, value.i)

    fun compare(func: CompareFunc, mode: CompareMode) {
        glTexParameteri(GL_TEXTURE_2D, GL14.GL_TEXTURE_COMPARE_FUNC, func.i)
        glTexParameteri(GL_TEXTURE_2D, GL14.GL_TEXTURE_COMPARE_MODE, mode.i)
    }

    enum class CompareMode(val i: Int) { rToTexture(GL14.GL_COMPARE_R_TO_TEXTURE), none(GL_NONE) }
    enum class CompareFunc(val i: Int) { lessEqual(GL_LEQUAL), greaterEqual(GL_GEQUAL), less(GL_LESS),
        greater(GL_GREATER), equal(GL_EQUAL), notEqual(GL_NOTEQUAL), always(GL_ALWAYS), never(GL_NEVER)
    }
}

object Textures {

    var target = 0
    lateinit var names: IntBuffer

    fun image(level: Int, internalFormat: Int, width: Int, format: Int, type: Int, pixels: ByteBuffer) =
            GL11.glTexImage1D(target, level, internalFormat, width, 0, format, type, pixels)

    fun image(level: Int, internalFormat: Int, width: Int, height: Int, format: Int, type: Int, pixels: ByteBuffer) =
            GL11.glTexImage2D(target, level, internalFormat, width, height, 0, format, type, pixels)

    inline fun at1d(index: Int, block: Texture1d.() -> Unit) {
        Texture1d.name = names[index] // bind
        Texture1d.block()
    }

    inline fun at2d(index: Int, block: Texture2d.() -> Unit) {
        Texture2d.name = names[index] // bind
        Texture2d.block()
    }
}

object Textures2d {

    lateinit var names: IntBuffer

    fun image(level: Int, internalFormat: Int, width: Int, height: Int, format: Int, type: Int, pixels: ByteBuffer) =
            GL11.glTexImage2D(GL_TEXTURE_2D, level, internalFormat, width, height, 0, format, type, pixels)

    inline fun at(index: Int, block: Texture2d.() -> Unit) {
        Texture2d.name = names[index] // bind
        Texture2d.block()
    }
}