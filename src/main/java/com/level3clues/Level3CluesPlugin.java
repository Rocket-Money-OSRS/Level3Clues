package com.level3clues;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.game.ItemManager;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.cluescrolls.ClueScrollPlugin;
import net.runelite.client.plugins.cluescrolls.ClueScrollConfig;
import net.runelite.client.plugins.cluescrolls.clues.ClueScroll;
import net.runelite.client.plugins.cluescrolls.clues.MapClue;
import net.runelite.client.plugins.cluescrolls.clues.MusicClue;
import net.runelite.client.plugins.cluescrolls.clues.CoordinateClue;
import net.runelite.client.plugins.cluescrolls.clues.AnagramClue;
import net.runelite.client.plugins.cluescrolls.clues.CipherClue;
import net.runelite.client.plugins.cluescrolls.clues.CrypticClue;
import net.runelite.client.plugins.cluescrolls.clues.EmoteClue;
import net.runelite.client.plugins.cluescrolls.clues.FairyRingClue;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.OverlayManager;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@PluginDescriptor(
	name = "Level 3 Clues",
	description = "Indicates whether clues are good or bad for level 3 skillers",
	tags = {"clues", "skiller", "level3"}
)
@PluginDependency(ClueScrollPlugin.class)
@Slf4j
public class Level3CluesPlugin extends Plugin
{
	@Inject
	private ClueScrollPlugin clueScrollPlugin;

	@Inject
	private Level3CluesConfig config;

	public Level3CluesConfig getConfig()
	{
		return config;
	}

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private Level3CluesOverlay overlay;

	@Inject
	private Level3CluesWorldOverlay worldOverlay;

	@Inject
	private ItemManager itemManager;

	@Inject
	private Client client;

	@Inject
	private com.level3clues.evaluator.SkillerClueEvaluator evaluator;

	@Inject
	private ConfigManager configManager;

	@Getter
	private final Map<Integer, ClueScroll> trackedClues = new ConcurrentHashMap<>();

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event)
	{
		if (!config.deprioritizeBadClues())
		{
			return;
		}

		if (!event.getOption().equals("Take"))
		{
			return;
		}

		int itemId = event.getIdentifier();
		if (itemId <= 0)
		{
			return;
		}

		ItemComposition itemComposition = itemManager.getItemComposition(itemId);
		if (itemComposition == null)
		{
			return;
		}

		String itemName = itemComposition.getName();
		if (itemName == null || (!itemName.startsWith("Clue scroll")
			&& !itemName.startsWith("Challenge scroll")
			&& !itemName.startsWith("Treasure scroll")))
		{
			return;
		}

		ClueScroll clue = findClueScroll(itemId);
		if (clue == null)
		{
			return;
		}

		if (shouldTrackOnPickup())
		{
			trackedClues.put(itemId, clue);
		}

		boolean isGood = evaluator.isClueGoodForSkiller(clue, clueScrollPlugin);
		if (!isGood)
		{
			MenuEntry[] menuEntries = client.getMenuEntries();
			int takeIndex = -1;
			int walkHereIndex = -1;

			for (int i = 0; i < menuEntries.length; i++)
			{
				MenuEntry entry = menuEntries[i];
				if (entry.getIdentifier() == itemId && entry.getOption().equals("Take"))
				{
					takeIndex = i;
				}
				else if (entry.getOption().equals("Walk here"))
				{
					walkHereIndex = i;
				}
			}

			if (takeIndex >= 0 && walkHereIndex >= 0 && takeIndex < walkHereIndex)
			{
				MenuEntry takeEntry = menuEntries[takeIndex];
				MenuEntry walkEntry = menuEntries[walkHereIndex];
				menuEntries[takeIndex] = walkEntry;
				menuEntries[walkHereIndex] = takeEntry;
				client.setMenuEntries(menuEntries);
			}
			else if (takeIndex >= 0)
			{
				menuEntries[takeIndex].setDeprioritized(true);
			}
		}
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		if (event.getMenuOption() == null)
		{
			return;
		}

		String menuOption = event.getMenuOption();
		boolean shouldTrack = false;

		if (menuOption.equals("Take") && shouldTrackOnPickup())
		{
			shouldTrack = true;
		}
		else if (menuOption.equals("Read") && !shouldTrackOnPickup())
		{
			shouldTrack = true;
		}

		if (shouldTrack)
		{
			int itemId = event.getItemId();
			if (itemId <= 0)
			{
				return;
			}

			ItemComposition itemComposition = itemManager.getItemComposition(itemId);
			if (itemComposition != null && (itemComposition.getName().startsWith("Clue scroll")
				|| itemComposition.getName().startsWith("Challenge scroll")
				|| itemComposition.getName().startsWith("Treasure scroll")))
			{
				ClueScroll clue = findClueScroll(itemId);
				if (clue != null)
				{
					trackedClues.put(itemId, clue);
				}
			}
		}
	}


	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(overlay);
		overlayManager.add(worldOverlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(overlay);
		overlayManager.remove(worldOverlay);
	}

	@Provides
	Level3CluesConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(Level3CluesConfig.class);
	}

	public ClueScrollPlugin getClueScrollPlugin()
	{
		return clueScrollPlugin;
	}

	private boolean shouldTrackOnPickup()
	{
		ClueScrollConfig clueScrollConfig = configManager.getConfig(ClueScrollConfig.class);
		return clueScrollConfig.identify() == ClueScrollConfig.IdentificationMode.ON_PICKUP;
	}

	private ClueScroll findClueScroll(int itemId)
	{
		if (itemId == ItemID.TRAIL_CLUE_BEGINNER || itemId == ItemID.TRAIL_CLUE_MASTER)
		{
			return null;
		}

		ClueScroll clue = MapClue.forItemId(itemId);
		if (clue != null)
		{
			return clue;
		}

		clue = MusicClue.forItemId(itemId);
		if (clue != null)
		{
			return clue;
		}

		clue = CoordinateClue.forItemId(itemId);
		if (clue != null)
		{
			return clue;
		}

		clue = AnagramClue.forItemId(itemId);
		if (clue != null)
		{
			return clue;
		}

		clue = CipherClue.forItemId(itemId);
		if (clue != null)
		{
			return clue;
		}

		clue = CrypticClue.forItemId(itemId);
		if (clue != null)
		{
			return clue;
		}

		clue = EmoteClue.forItemId(itemId);
		if (clue != null)
		{
			return clue;
		}

		clue = FairyRingClue.forItemId(itemId);
		if (clue != null)
		{
			return clue;
		}

		return null;
	}
}

