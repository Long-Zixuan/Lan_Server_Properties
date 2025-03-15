package rikka.lanserverproperties.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.ShareToLanScreen;
import net.minecraft.network.chat.Component;

import rikka.lanserverproperties.IShareToLanScreenParamAccessor;
import rikka.lanserverproperties.OpenToLanScreenEx;

@Mixin(ShareToLanScreen.class)
public abstract class MixinOpenToLanScreenFabric extends Screen {
	protected MixinOpenToLanScreenFabric(Component pTitle) {
		super(pTitle);
	}

	@SuppressWarnings("unchecked")
	@Unique
	private final <R extends Renderable, G extends GuiEventListener & NarratableEntry> void add(GuiEventListener b) {
		if (b instanceof GuiEventListener) {
			if (b instanceof NarratableEntry) {
				this.addWidget((G) b);
			} else {
				((List<GuiEventListener>)this.children()).add(b);
			}
		}
		if (b instanceof Renderable)
			this.addRenderableOnly((R) b);
	}

	@Unique
	private void remove(GuiEventListener widget) {
		this.removeWidget(widget);
	}

	@Inject(method = "init", at = @At("TAIL"))
	protected void mimicForge_GuiPostInit(CallbackInfo ci) {
		((IShareToLanScreenParamAccessor)this).getLSPData().postInitShareToLanScreen(
				this.font, this.children(), this::add, this::remove);
	}

	@Inject(method = "render", at = @At("TAIL"))
	public void mimicForge_GuiPostRender(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
		OpenToLanScreenEx.postDraw(this, this.font, guiGraphics, mouseX, mouseY, partialTicks);
	}
}
