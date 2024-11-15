package org.stardustmodding.skyengine.client.entity

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.*
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.Minecraft
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.client.renderer.RenderType
import org.joml.Matrix4f
import org.joml.Vector3f
import org.stardustmodding.skyengine.entity.ShipEntity
import org.stardustmodding.skyengine.math.PosExt.toBlockPos
import org.stardustmodding.skyengine.math.PosExt.toVec3d
import org.stardustmodding.skyengine.plotyard.PlotyardManager
import java.util.*

@Environment(EnvType.CLIENT)
class ShipModel : EntityModel<ShipEntity>() {
    var entity: ShipEntity? = null

    override fun setupAnim(
        entity: ShipEntity,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
    }

    override fun renderToBuffer(
        stack: PoseStack, cons: VertexConsumer, packedLight: Int, packedOverlay: Int, color: Int
    ) {
        if (entity != null) {
            val buffer = buffers.computeIfAbsent(entity!!.uuid) { createMesh() }
            val renderType = RenderType.solid()

            if (buffer == null) return

            renderType.setupRenderState()
            RenderSystem.setShader(GameRenderer::getRendertypeSolidShader)

            val projMatrix = RenderSystem.getProjectionMatrix()
            val shader = RenderSystem.getShader()!!

            buffer.bind()
            shader.apply()

            val modelViewMat = shader.MODEL_VIEW_MATRIX!!
            val projMat = shader.PROJECTION_MATRIX!!
            val chunkOffset = shader.CHUNK_OFFSET!!

            modelViewMat.set(RenderSystem.getModelViewMatrix().mul(stack.last().pose(), Matrix4f()))
            projMat.set(projMatrix)
            chunkOffset.set(Vector3f(0.0f, 0.0f, 0.0f))

            modelViewMat.upload()
            projMat.upload()
            chunkOffset.upload()

            buffer.draw()
        }
    }

    private fun createMesh(): VertexBuffer? {
        val buffer = VertexBuffer(VertexBuffer.Usage.DYNAMIC)
        val stack = PoseStack()
        val blocks = entity!!.blocks.get()
        val client = Minecraft.getInstance()
        val mgr = client.blockRenderer
        val build = BufferBuilder(ByteBufferBuilder(4096), VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK)
        val plotyard = PlotyardManager[entity!!.level]
        val getter = plotyard.getBlockGetter(entity!!.level, entity!!.uuid) ?: return null

        for ((relPos, state) in blocks) {
            stack.pushPose()
            stack.translate(relPos.x.toDouble(), relPos.y.toDouble(), relPos.z.toDouble())

            val pos = entity!!.position.add(relPos.toVec3d()).toBlockPos()

            mgr.renderBatched(state, pos, getter, stack, build, true, entity!!.level.random)
            stack.translate(-relPos.x.toDouble(), -relPos.y.toDouble(), -relPos.z.toDouble())
            stack.popPose()
        }

        val mesh = build.buildOrThrow()

        buffer.bind()
        buffer.upload(mesh)

        VertexBuffer.unbind()

        return buffer
    }

    companion object {
        private var buffers: MutableMap<UUID, VertexBuffer?> = mutableMapOf()

        fun removeEntity(uuid: UUID) {
            buffers.remove(uuid)?.close()
        }

        fun getTexturedModelData(): LayerDefinition {
            return LayerDefinition.create(MeshDefinition(), 0, 0)
        }
    }
}
