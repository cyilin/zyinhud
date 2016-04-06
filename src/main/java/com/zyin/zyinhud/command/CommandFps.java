package com.zyin.zyinhud.command;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

import com.zyin.zyinhud.mods.Fps;
import net.minecraft.server.MinecraftServer;

/**
 * The type Command fps.
 */
public class CommandFps extends CommandBase
{
	@Override
	public String getCommandName()
	{
		return "fps";
	}
	
	@Override
    public List getCommandAliases()
    {
		List list = new ArrayList();
		list.add("f");
        return list;
    }

	/**
	 * Process command.
	 * @param minecraftServer
	 * @param iCommandSender
	 * @param strings
	 * @throws CommandException
     */
	@Override
	public void execute(MinecraftServer minecraftServer, ICommandSender iCommandSender, String[] strings) throws CommandException {
		Fps.ToggleEnabled();
	}

	@Override
	public String getCommandUsage(ICommandSender iCommandSender)
	{
		return "commands.zyinhudfps.usage";
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
		Fps.ToggleEnabled();
	}
	*/
}
