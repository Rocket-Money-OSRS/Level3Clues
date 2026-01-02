package com.level3clues.evaluator.util;

import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.cluescrolls.clues.item.AllRequirementsCollection;
import net.runelite.client.plugins.cluescrolls.clues.item.AnyRequirementCollection;
import net.runelite.client.plugins.cluescrolls.clues.item.ItemRequirement;
import net.runelite.client.plugins.cluescrolls.clues.item.RangeItemRequirement;
import net.runelite.client.plugins.cluescrolls.clues.item.SingleItemRequirement;

@Singleton
public class ItemRequirementHelper
{
	@Inject
	private Client client;

	@Inject
	private ItemManager itemManager;

	public int getItemIdFromSingleRequirement(SingleItemRequirement requirement)
	{
		String name = requirement.getCollectiveName(client);
		if (name == null || name.equals("N/A"))
		{
			return -1;
		}

		for (int itemId = 1; itemId < 100000; itemId++)
		{
			ItemComposition def = itemManager.getItemComposition(itemId);
			if (def != null && name.equals(def.getName()) && requirement.fulfilledBy(itemId))
			{
				return itemId;
			}
		}
		return -1;
	}

	public ItemRequirement[] getSubRequirements(AnyRequirementCollection collection)
	{
		java.util.List<ItemRequirement> requirements = new java.util.ArrayList<>();
		for (int itemId = 1; itemId < 100000; itemId++)
		{
			if (collection.fulfilledBy(itemId))
			{
				ItemComposition def = itemManager.getItemComposition(itemId);
				if (def != null && def.getName() != null && !def.getName().equals("null"))
				{
					requirements.add(new SingleItemRequirement(itemId));
				}
			}
		}
		return requirements.toArray(new ItemRequirement[0]);
	}

	public ItemRequirement[] getSubRequirements(AllRequirementsCollection collection)
	{
		java.util.List<ItemRequirement> requirements = new java.util.ArrayList<>();
		for (int itemId = 1; itemId < 100000; itemId++)
		{
			if (collection.fulfilledBy(itemId))
			{
				ItemComposition def = itemManager.getItemComposition(itemId);
				if (def != null && def.getName() != null && !def.getName().equals("null"))
				{
					requirements.add(new SingleItemRequirement(itemId));
				}
			}
		}
		return requirements.toArray(new ItemRequirement[0]);
	}

	public int getStartItemId(RangeItemRequirement requirement)
	{
		for (int itemId = 1; itemId < 100000; itemId++)
		{
			if (requirement.fulfilledBy(itemId))
			{
				ItemComposition def = itemManager.getItemComposition(itemId);
				if (def != null && def.getName() != null && !def.getName().equals("null"))
				{
					return itemId;
				}
			}
		}
		return -1;
	}

	public int getEndItemId(RangeItemRequirement requirement)
	{
		int lastItemId = -1;
		for (int itemId = 1; itemId < 100000; itemId++)
		{
			if (requirement.fulfilledBy(itemId))
			{
				ItemComposition def = itemManager.getItemComposition(itemId);
				if (def != null && def.getName() != null && !def.getName().equals("null"))
				{
					lastItemId = itemId;
				}
			}
		}
		return lastItemId;
	}
}

