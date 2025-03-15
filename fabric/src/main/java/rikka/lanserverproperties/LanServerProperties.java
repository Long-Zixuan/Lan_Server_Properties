package rikka.lanserverproperties;

import net.minecraft.client.server.IntegratedServer;
import net.minecraft.server.players.PlayerList;
import rikka.lanserverproperties.mixin.PlayerListAccessor;

public class LanServerProperties {
	/**
	 *  Called by the mod, should be consistent in all implementations.
	 */
	public static void setMaxPlayers(IntegratedServer server, int num) {
		PlayerList playerList = server.getPlayerList();
		((PlayerListAccessor)playerList).setMaxPlayers(num);
	}

	/**
	 * These are the lambda names used for Mixin injection, should be consistent in all implementations.
	 */
	// m_279789_(Lnet/minecraft/client/server/IntegratedServer;Lnet/minecraft/client/gui/components/Button;)V
	public static final String mixin_startButton_OnClick = "method_19851";

	// m_257075_(Lnet/minecraft/client/gui/components/Button;Ljava/lang/String;)V
	public static final String mixin_portEditBox_OnChange = "method_47416";
}
