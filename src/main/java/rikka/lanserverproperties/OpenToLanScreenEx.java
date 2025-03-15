package rikka.lanserverproperties;

import java.net.InetAddress;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class OpenToLanScreenEx
{
	private final static int defaultPort = 8888;

	private final static String onlineModeLangKey = "lanserverproperties.gui.online_mode";
	private final static String onlinemodeDescLangKey = "lanserverproperties.gui.online_mode_desc";
	private final static String portLangKey = "lanserverproperties.gui.port";
	private final static Text onlinemodeDescTooltip = new TranslatableText(onlinemodeDescLangKey);
	private final static Text portDescLabel = new TranslatableText(portLangKey);

	private static Text getOnlineButtonText(boolean onlineMode)
	{
		return new LiteralText(I18n.translate(onlineModeLangKey) + ": "
				+ I18n.translate(onlineMode ? "lanserverproperties.gui.notallow" : "lanserverproperties.gui.allow"));
	}

	public static class WidgetGroup extends InvisibleWidgetGroup
	{
		public ToggleButton onlineModeButton;
		public IPAddressTextField tfwPort;
		public TextFieldWidget msgText;

		public TextFieldWidget wifiAndIPText;
	}

	/**
	 * Forge: GuiScreenEvent.InitGuiEvent.Post
	 */
	public static void init(Screen gui, TextRenderer textRenderer, List<AbstractButtonWidget> widgets, Consumer<AbstractButtonWidget> widgetAdder)
	{
		WidgetGroup group = new WidgetGroup();
		widgetAdder.accept(group);

		// Add our own widgets
		// Toggle button for onlineMode
		group.onlineModeButton = 
				new ToggleButton(gui.width / 2 - 155, 124, 310, 20,
					OpenToLanScreenEx::getOnlineButtonText, true, 
					(screen, matrixStack, mouseX, mouseY) -> gui.renderTooltip(matrixStack, onlinemodeDescTooltip, mouseX, mouseY)
				);
		widgetAdder.accept(group.onlineModeButton);


		group.msgText = new TextFieldWidget(textRenderer, gui.width / 2 - 153 + 80, gui.height - 54,
				225, 20, new TranslatableText("lanserverproperties.gui.port_msg"));
		group.msgText.setEditable(false);
		group.msgText.setUneditableColor(0x4D5B58);
		group.msgText.setMaxLength(100);
		group.msgText.setText(" ");
		widgetAdder.accept(group.msgText);

		// Text field for port
		group.tfwPort =
				new IPAddressTextField(textRenderer, gui.width / 2 - 153, gui.height - 54, 80, 20,
					portDescLabel, defaultPort, group.msgText);
		widgetAdder.accept(group.tfwPort);

///////////////////////////////////////////////////////////////////////////////////////////////////

		group.wifiAndIPText = new TextFieldWidget(textRenderer, gui.width / 2 - 153, gui.height / 2 + 35, 305, 20,new TranslatableText("IP And WIFI"));
		group.wifiAndIPText.setEditable(false);
		group.wifiAndIPText.setUneditableColor(0x66CCFF);
		String ipAddress = "Unknown";
		try
		{
			InetAddress addr = InetAddress.getLocalHost();
			//System.out.println("Local HostAddress: "+addr.getHostAddress());
			ipAddress = addr.getHostAddress();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		group.wifiAndIPText.setMaxLength(100);
		group.wifiAndIPText.setText("IP:"+ipAddress+"    WIFI:"+getWiFiName());
		widgetAdder.accept(group.wifiAndIPText);
	}

	public static String getWiFiName()
	{
		try
		{
			Process process = Runtime.getRuntime().exec("netsh wlan show interfaces");
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null)
			{
				if (line.contains("SSID"))
				{
					return line.split(":")[1].trim();
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return "None";
	}

	/**
	 * Forge: GuiScreenEvent.DrawScreenEvent.Post
	 */
	public static void postDraw(Screen gui, TextRenderer textRenderer, MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{
		Screen.drawTextWithShadow(matrixStack, textRenderer, portDescLabel, gui.width / 2 - 155, gui.height - 66, 10526880);
	}

	public static int getServerPort(Screen gui)
	{
		WidgetGroup group = WidgetGroup.fromScreen(gui, WidgetGroup.class);

		if (group != null)
		{
			return group.tfwPort.getServerPort();
		}

		return defaultPort;
	}

	public static void onOpenToLanSuccess(Screen gui)
	{
		WidgetGroup group = WidgetGroup.fromScreen(gui, WidgetGroup.class);

		if (group != null)
		{
			boolean onlineMode = group.onlineModeButton.getState();
			MinecraftClient.getInstance().getServer().setOnlineMode(onlineMode);
		}
	}
}
