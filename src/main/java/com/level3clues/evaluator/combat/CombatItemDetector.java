package com.level3clues.evaluator.combat;

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
import com.level3clues.evaluator.util.ItemRequirementHelper;

@Singleton
public class CombatItemDetector
{
	private static final java.util.Set<String> COMBAT_CHALLENGE_KEYWORDS = java.util.Set.of(
		"kill", "slay", "defeat", "fight", "combat", "attack", "destroy", "eliminate"
	);

	@Inject
	private Client client;

	@Inject
	private ItemManager itemManager;

	@Inject
	private ItemRequirementHelper itemRequirementHelper;

	public boolean requiresCombat(String challengeText)
	{
		if (challengeText == null)
		{
			return false;
		}
		String lowerText = challengeText.toLowerCase();
		return COMBAT_CHALLENGE_KEYWORDS.stream().anyMatch(lowerText::contains);
	}

	public boolean hasCombatRequirements(ItemRequirement[] requirements)
	{
		if (requirements == null || requirements.length == 0)
		{
			return false;
		}

		for (ItemRequirement requirement : requirements)
		{
			if (hasCombatRequirement(requirement))
			{
				return true;
			}
		}

		return false;
	}

	private boolean hasCombatRequirement(ItemRequirement requirement)
	{
		if (requirement instanceof SingleItemRequirement)
		{
			SingleItemRequirement singleReq = (SingleItemRequirement) requirement;
			String itemName = singleReq.getCollectiveName(client);
			if (itemName != null && !itemName.equals("N/A"))
			{
				return hasCombatItemName(itemName);
			}
			int itemId = itemRequirementHelper.getItemIdFromSingleRequirement(singleReq);
			if (itemId != -1)
			{
				return checkItemCombatRequirement(itemId);
			}
		}
		else if (requirement instanceof RangeItemRequirement)
		{
			RangeItemRequirement rangeReq = (RangeItemRequirement) requirement;
			String itemName = rangeReq.getCollectiveName(client);
			if (itemName != null && !itemName.equals("N/A"))
			{
				return hasCombatItemName(itemName);
			}
			int start = itemRequirementHelper.getStartItemId(rangeReq);
			int end = itemRequirementHelper.getEndItemId(rangeReq);
			return checkItemRangeCombatRequirement(start, end);
		}
		else if (requirement instanceof AnyRequirementCollection)
		{
			AnyRequirementCollection anyReq = (AnyRequirementCollection) requirement;
			String itemName = anyReq.getCollectiveName(client);
			if (itemName != null && !itemName.equals("N/A"))
			{
				return hasCombatItemName(itemName);
			}
			ItemRequirement[] subRequirements = itemRequirementHelper.getSubRequirements(anyReq);
			if (subRequirements == null || subRequirements.length == 0)
			{
				return false;
			}
			boolean allHaveCombatRequirements = true;
			for (ItemRequirement subReq : subRequirements)
			{
				if (!hasCombatRequirement(subReq))
				{
					allHaveCombatRequirements = false;
					break;
				}
			}
			return allHaveCombatRequirements;
		}
		else if (requirement instanceof AllRequirementsCollection)
		{
			AllRequirementsCollection allReq = (AllRequirementsCollection) requirement;
			String itemName = allReq.getCollectiveName(client);
			if (itemName != null && !itemName.equals("N/A"))
			{
				return hasCombatItemName(itemName);
			}
			ItemRequirement[] subRequirements = itemRequirementHelper.getSubRequirements(allReq);
			for (ItemRequirement subReq : subRequirements)
			{
				if (hasCombatRequirement(subReq))
				{
					return true;
				}
			}
		}

		return false;
	}

	private boolean hasCombatItemName(String itemName)
	{
		if (itemName == null)
		{
			return false;
		}

		String lowerName = itemName.toLowerCase();

		if (lowerName.contains("halberd") && (lowerName.contains("adamant") || lowerName.contains("rune") || 
		    lowerName.contains("dragon") || lowerName.contains("mithril") || lowerName.contains("steel")))
		{
			return true;
		}

		if (lowerName.contains("mystic") && (lowerName.contains("robe") || lowerName.contains("hat") || 
		    lowerName.contains("boots") || lowerName.contains("gloves")))
		{
			return true;
		}

		if ((lowerName.contains("rune") || lowerName.contains("adamant") || lowerName.contains("mithril") || 
		     lowerName.contains("steel") || lowerName.contains("dragon")) && 
		    (lowerName.contains("platebody") || lowerName.contains("platelegs") || lowerName.contains("plateskirt") ||
		     lowerName.contains("chainbody") || lowerName.contains("full helm") || lowerName.contains("kiteshield") ||
		     lowerName.contains("sq shield") || lowerName.contains("sword") || lowerName.contains("scimitar") ||
		     lowerName.contains("longsword") || lowerName.contains("battleaxe") || lowerName.contains("warhammer") ||
		     lowerName.contains("2h sword") || lowerName.contains("mace") || lowerName.contains("dagger")))
		{
			return true;
		}

		if (lowerName.contains("d'hide") || lowerName.contains("leather") || lowerName.contains("studded") ||
		    lowerName.contains("snakeskin") || lowerName.contains("karil") || lowerName.contains("armadyl"))
		{
			if (lowerName.contains("body") || lowerName.contains("chaps") || lowerName.contains("vambraces") ||
			    lowerName.contains("coif") || lowerName.contains("boots"))
			{
				return true;
			}
		}

		return false;
	}

	private boolean checkItemCombatRequirement(int itemId)
	{
		if (itemId == -1)
		{
			return false;
		}

		ItemComposition itemDef = itemManager.getItemComposition(itemId);
		if (itemDef == null)
		{
			return false;
		}

		return hasCombatItemName(itemDef.getName());
	}

	private boolean checkItemRangeCombatRequirement(int startItemId, int endItemId)
	{
		int maxCheck = Math.min(endItemId, startItemId + 100);
		for (int itemId = startItemId; itemId <= maxCheck; itemId++)
		{
			if (checkItemCombatRequirement(itemId))
			{
				return true;
			}
		}
		return false;
	}
}

