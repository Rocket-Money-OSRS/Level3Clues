package com.level3clues.evaluator.location;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Singleton;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.cluescrolls.ClueScrollPlugin;
import net.runelite.client.plugins.cluescrolls.clues.ClueScroll;
import net.runelite.client.plugins.cluescrolls.clues.LocationClueScroll;

@Singleton
public class LocationValidator
{
	private static final Set<Integer> MORYTANIA_REGION_IDS = new HashSet<>(Arrays.asList(
		14388, 14389, 14390, 14391,
		14644, 14645, 14646, 14647,
		14899, 14900, 14901, 14902,
		15155, 15156, 15157, 15158
	));

	public boolean isInMorytania(ClueScroll clue, ClueScrollPlugin clueScrollPlugin)
	{
		if (clue instanceof LocationClueScroll)
		{
			LocationClueScroll locationClue = (LocationClueScroll) clue;
			WorldPoint location = locationClue.getLocation(clueScrollPlugin);
			if (location != null)
			{
				int regionId = location.getRegionID();
				return MORYTANIA_REGION_IDS.contains(regionId);
			}
		}
		return false;
	}
}

