package rikka.lanserverproperties.mixin;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.ShareToLanScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.GameType;

import rikka.lanserverproperties.IShareToLanScreenParamAccessor;
import rikka.lanserverproperties.LanServerProperties;
import rikka.lanserverproperties.OpenToLanScreenEx;

@Mixin(ShareToLanScreen.class)
public abstract class MixinOpenToLanScreen extends Screen implements IShareToLanScreenParamAccessor {
	@Unique
	private OpenToLanScreenEx lsp_objects;

	@Shadow
	@Final
	private Screen lastScreen;
	@Shadow
	private GameType gameMode;
	@Shadow
	private int port;
	@Shadow
	private EditBox portEdit;
	@Shadow
	private boolean commands;

	protected MixinOpenToLanScreen(Component text) {
		super(text);
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	private void patchInit_ShareToLanScreen(CallbackInfo ci) {
		this.lsp_objects = new OpenToLanScreenEx((ShareToLanScreen)(Object)this, (IShareToLanScreenParamAccessor)this);
	}

	@Inject(method = LanServerProperties.mixin_startButton_OnClick, at = @At("TAIL"))
	private void patch_startButton_OnClick(CallbackInfo ci) {
		this.lsp_objects.onOpenToLanClosed();
	}

	@Inject(method = LanServerProperties.mixin_portEditBox_OnChange, at = @At("TAIL"))
	private void patch_portEditBox_OnChange(CallbackInfo ci) {
		this.lsp_objects.onPortEditBoxChanged();
	}

	@Redirect(method = "tryParsePort",
			at = @At(value = "INVOKE", target = "net/minecraft/util/HttpUtil.getAvailablePort()I"))
	private int redirect_tryParsePort() {
		return this.lsp_objects.getDefaultPort();
	}

	@Redirect(method = "render",
			slice = @Slice(from = @At(value = "FIELD", ordinal = 0, opcode = Opcodes.GETSTATIC, target = "Lnet/minecraft/client/gui/screens/ShareToLanScreen;PORT_INFO_TEXT:Lnet/minecraft/network/chat/Component;")),
			at = @At(value = "INVOKE", target = "net/minecraft/client/gui/GuiGraphics.drawCenteredString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)V")
	)
	private void bypass_render_PortText(GuiGraphics guiGraphics, Font font, Component component, int i, int j, int k) {
	}

	@Override
	public OpenToLanScreenEx getLSPData() {
		return this.lsp_objects;
	}

	@Override
	public Font getFont() {
		return this.font;
	}

	///////////////////////////////////////////////////////////////////
	/// IShareToLanScreenParamAccessor
	///////////////////////////////////////////////////////////////////
	@Override
	public Screen getLastScreen() {
		return this.lastScreen;
	}

	@Override
	public GameType getGameType() {
		return this.gameMode;
	}

	@Override
	public boolean isCommandEnabled() {
		return this.commands;
	}

	@Override
	public int getPort() {
		return this.port;
	}

	@Override
	public void movePortEditBox(int x, int y, int width, int height) {
		this.portEdit.setPosition(x, y);
		this.portEdit.setWidth(width);
		((WidgetHeightAccessor) this.portEdit).setHeight(height);
	}

	@Override
	public void setGameType(GameType gameType) {
		this.gameMode = gameType;
	}

	@Override
	public void setCommandEnabled(boolean commandEnabled) {
		this.commands = commandEnabled;
	}

	@Override
	public void setPort(int port) {
		this.port = port;
	}
}
