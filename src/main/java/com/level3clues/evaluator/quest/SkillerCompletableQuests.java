package com.level3clues.evaluator.quest;

import java.util.Set;
import java.util.HashSet;

public class SkillerCompletableQuests
{
	public static final Set<String> COMPLETABLE_QUEST_NAMES = new HashSet<>();

	static
	{
		COMPLETABLE_QUEST_NAMES.add("Below Ice Mountain");
		COMPLETABLE_QUEST_NAMES.add("Black Knights' Fortress");
		COMPLETABLE_QUEST_NAMES.add("Cook's Assistant");
		COMPLETABLE_QUEST_NAMES.add("The Corsair Curse");
		COMPLETABLE_QUEST_NAMES.add("Demon Slayer");
		COMPLETABLE_QUEST_NAMES.add("Doric's Quest");
		COMPLETABLE_QUEST_NAMES.add("Ernest the Chicken");
		COMPLETABLE_QUEST_NAMES.add("Goblin Diplomacy");
		COMPLETABLE_QUEST_NAMES.add("The Knight's Sword");
		COMPLETABLE_QUEST_NAMES.add("Misthalin Mystery");
		COMPLETABLE_QUEST_NAMES.add("Pirate's Treasure");
		COMPLETABLE_QUEST_NAMES.add("Prince Ali Rescue");
		COMPLETABLE_QUEST_NAMES.add("Romeo & Juliet");
		COMPLETABLE_QUEST_NAMES.add("Rune Mysteries");
		COMPLETABLE_QUEST_NAMES.add("Shield of Arrav");
		COMPLETABLE_QUEST_NAMES.add("Sheep Shearer");
		COMPLETABLE_QUEST_NAMES.add("X Marks the Spot");

		COMPLETABLE_QUEST_NAMES.add("Druidic Ritual");
		COMPLETABLE_QUEST_NAMES.add("Lost City");
		COMPLETABLE_QUEST_NAMES.add("Merlin's Crystal");
		COMPLETABLE_QUEST_NAMES.add("Tribal Totem");
		COMPLETABLE_QUEST_NAMES.add("Fishing Contest");
		COMPLETABLE_QUEST_NAMES.add("Monk's Friend");
		COMPLETABLE_QUEST_NAMES.add("Clock Tower");
		COMPLETABLE_QUEST_NAMES.add("Hazeel Cult");
		COMPLETABLE_QUEST_NAMES.add("Sheep Herder");
		COMPLETABLE_QUEST_NAMES.add("Plague City");
		COMPLETABLE_QUEST_NAMES.add("Sea Slug");
		COMPLETABLE_QUEST_NAMES.add("Biohazard");
		COMPLETABLE_QUEST_NAMES.add("Jungle Potion");
		COMPLETABLE_QUEST_NAMES.add("Shilo Village");
		COMPLETABLE_QUEST_NAMES.add("Observatory Quest");
		COMPLETABLE_QUEST_NAMES.add("The Tourist Trap");
		COMPLETABLE_QUEST_NAMES.add("Dwarf Cannon");
		COMPLETABLE_QUEST_NAMES.add("Murder Mystery");
		COMPLETABLE_QUEST_NAMES.add("The Dig Site");
		COMPLETABLE_QUEST_NAMES.add("Gertrude's Cat");
		COMPLETABLE_QUEST_NAMES.add("Elemental Workshop I");
		COMPLETABLE_QUEST_NAMES.add("Tai Bwo Wannai Trio");
		COMPLETABLE_QUEST_NAMES.add("One Small Favour");
		COMPLETABLE_QUEST_NAMES.add("The Feud");
		COMPLETABLE_QUEST_NAMES.add("The Golem");
		COMPLETABLE_QUEST_NAMES.add("Icthlarin's Little Helper");
		COMPLETABLE_QUEST_NAMES.add("Tears of Guthix");
		COMPLETABLE_QUEST_NAMES.add("The Lost Tribe");
		COMPLETABLE_QUEST_NAMES.add("A Tail of Two Cats");
		COMPLETABLE_QUEST_NAMES.add("Ratcatchers");
		COMPLETABLE_QUEST_NAMES.add("The Hand in the Sand");
		COMPLETABLE_QUEST_NAMES.add("Recipe for Disaster");
		COMPLETABLE_QUEST_NAMES.add("Enlightened Journey");
		COMPLETABLE_QUEST_NAMES.add("Eagles' Peak");
		COMPLETABLE_QUEST_NAMES.add("Contact!");
		COMPLETABLE_QUEST_NAMES.add("Cold War");
		COMPLETABLE_QUEST_NAMES.add("Tower of Life");
		COMPLETABLE_QUEST_NAMES.add("Client of Kourend");
		COMPLETABLE_QUEST_NAMES.add("Bone Voyage");
		COMPLETABLE_QUEST_NAMES.add("The Queen of Thieves");
		COMPLETABLE_QUEST_NAMES.add("The Depths of Despair");
		COMPLETABLE_QUEST_NAMES.add("The Forsaken Tower");
		COMPLETABLE_QUEST_NAMES.add("The Ascent of Arceuus");
		COMPLETABLE_QUEST_NAMES.add("A Porcine of Interest");
		COMPLETABLE_QUEST_NAMES.add("Getting Ahead");
		COMPLETABLE_QUEST_NAMES.add("Temple of the Eye");
		COMPLETABLE_QUEST_NAMES.add("Sleeping Giants");
		COMPLETABLE_QUEST_NAMES.add("The Garden of Death");
		COMPLETABLE_QUEST_NAMES.add("Children of the Sun");
		COMPLETABLE_QUEST_NAMES.add("The Ribbiting Tale of a Lily Pad Labour Dispute");
		COMPLETABLE_QUEST_NAMES.add("At First Light");
		COMPLETABLE_QUEST_NAMES.add("Twilight's Promise");
		COMPLETABLE_QUEST_NAMES.add("Death on the Isle");
		COMPLETABLE_QUEST_NAMES.add("Ethically Acquired Antiquities");
		COMPLETABLE_QUEST_NAMES.add("Scrambled!");
		COMPLETABLE_QUEST_NAMES.add("Shadows of Custodia");
		COMPLETABLE_QUEST_NAMES.add("Pandemonium");
		COMPLETABLE_QUEST_NAMES.add("Prying Times");
		COMPLETABLE_QUEST_NAMES.add("Current Affairs");
		COMPLETABLE_QUEST_NAMES.add("Troubled Tortugans");

		COMPLETABLE_QUEST_NAMES.add("Watchtower");
	}

	public static boolean isSkillerCompletable(String questName)
	{
		if (questName == null)
		{
			return false;
		}
		String normalized = normalizeQuestName(questName);
		return COMPLETABLE_QUEST_NAMES.contains(normalized);
	}

	private static String normalizeQuestName(String questName)
	{
		if (questName == null)
		{
			return "";
		}
		questName = questName.trim();
		if (questName.startsWith("the ") || questName.startsWith("The "))
		{
			questName = questName.substring(4);
		}
		if (questName.endsWith("'s") || questName.endsWith("'S"))
		{
			questName = questName.substring(0, questName.length() - 2) + "s";
		}
		return questName;
	}
}

