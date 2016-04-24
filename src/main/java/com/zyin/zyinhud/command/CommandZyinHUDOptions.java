package com.zyin.zyinhud.command;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

import com.zyin.zyinhud.gui.GuiZyinHUDOptions;
import net.minecraft.server.MinecraftServer;

/**
 * The type Command zyin hud options.
 */
public class CommandZyinHUDOptions extends CommandBase
{
	private static Minecraft mc = Minecraft.getMinecraft();
	
	@Override
	public String getCommandName()
	{
		return "zyinhud";
	}
	
	@Override
    public List getCommandAliases()
    {
		List list = new ArrayList();
		list.add("zh");
		list.add("zyin");
		list.add("zyinshud");
		list.add("zyinoptions");
		list.add("zyinhudoptions");
		list.add("zyinshudoptions");
        return list;
    }

	/**
	 * Process command.
	 *
	 * @param minecraftServer
	 * @param iCommandSender
	 * @param strings
	 * @throws CommandException
     */
	@Override
	public void execute(MinecraftServer minecraftServer, ICommandSender iCommandSender, String[] strings) throws CommandException {
		mc.displayGuiScreen(new GuiZyinHUDOptions(null));
	}

	@Override
	public String getCommandUsage(ICommandSender iCommandSender)
	{
		return "commands.zyinhudoptions.usage";
	}


	/**
	 * Process command.
	 *
	 * @param iCommandSender the command sender
	 * @param parameters     the parameters
	 */
	/*
	@Override
	public void processCommand(ICommandSender iCommandSender, String[] parameters)
	{
		mc.displayGuiScreen(new GuiZyinHUDOptions(null));
	}
	*/
}
