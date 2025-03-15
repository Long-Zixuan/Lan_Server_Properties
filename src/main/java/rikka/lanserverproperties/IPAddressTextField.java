package rikka.lanserverproperties;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import net.minecraft.client.resource.language.I18n;


public class IPAddressTextField extends TextFieldWidget
{

	private final int defaultPort;

	private TextFieldWidget portMsg;

	public IPAddressTextField(TextRenderer textRenderer, int x, int y, int width, int height, Text name, int defaultPort,TextFieldWidget portMsg)
	{
		super(textRenderer, x, y, width, height, name);
		this.defaultPort = defaultPort;
		this.setText(String.valueOf(this.defaultPort));
		// Check the format, make sure the text is a valid integer
		//this.setChangedListener((text) -> this.setEditableColor(validatePort(text) >= 0 ? 0xFFFFFF : 0xFF0000));
		this.setChangedListener(this::colLogic);
		setMaxLength(5);
		this.portMsg = portMsg;
	}

	private void colLogic(String text)//条件表达式写这个可读性太差了
	{
		if(validatePort(text) >= 0)
		{
			if(Integer.parseInt(text) >= 49152)//从49152到65535是动态和/或私有端口，不建议使用
			{
				this.setEditableColor(0xED65FF);
				this.portMsg.setText(new TranslatableText("lanserverproperties.gui.port_warm").getString());
				this.portMsg.setCursorToStart();
				return;
			}
			this.setEditableColor(0xFFFFFF);
			this.portMsg.setText(new TranslatableText("lanserverproperties.gui.no_problem").getString());
			this.portMsg.setCursorToStart();
			return;
		}
		this.setEditableColor(0xFF0000);
		this.portMsg.setText(new TranslatableText("lanserverproperties.gui.port_error").getString());
		this.portMsg.setCursorToStart();

	}


	public int getServerPort()
	{
		String portStr = getText();
		return validatePort(portStr) >= 0 ? Integer.parseInt(portStr) : 25565;//不返回默认端口仅仅是为了区分两种情况，无其他含义
	}

	/**
	 * @param text
	 * @return negative if port is invalid, otherwise the port number
	 */
	public static int validatePort(String text)
	{
		boolean valid = true;
		int port = -1;
		try
		{
			if (text.length() > 0)
			{
				port = Integer.parseInt(text);
				if (port < 1024 || port > 65535)//端口小于1024也是不行的,0-1023是公认端口，不能使用，使用游戏会启动失败
				{
					valid = false;
				}
			}
			else
			{
				valid = false;
			}
		}
		catch (NumberFormatException e)
		{
			valid = false;
		}

		return valid ? port : -1;
	}
}
