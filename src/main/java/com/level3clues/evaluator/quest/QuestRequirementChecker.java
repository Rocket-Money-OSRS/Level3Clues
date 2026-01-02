package com.level3clues.evaluator.quest;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.cluescrolls.ClueScrollPlugin;
import net.runelite.client.plugins.cluescrolls.clues.ClueScroll;
import net.runelite.client.plugins.cluescrolls.clues.CrypticClue;
import net.runelite.client.plugins.cluescrolls.clues.EmoteClue;
import net.runelite.client.plugins.cluescrolls.clues.LocationClueScroll;
import net.runelite.client.plugins.cluescrolls.clues.SkillChallengeClue;
import net.runelite.client.plugins.cluescrolls.clues.item.SingleItemRequirement;
import com.level3clues.evaluator.util.ItemRequirementHelper;

@Singleton
public class QuestRequirementChecker
{
	private static final Set<Integer> QUEST_LOCKED_CLUE_ITEM_IDS = new HashSet<>();
	private static final Set<WorldPoint> QUEST_LOCKED_CLUE_LOCATIONS = new HashSet<>();
	private static final Set<String> QUEST_LOCKED_CLUE_TEXTS = new HashSet<>();

	static
	{
		populateQuestLockedClues();
	}

	private static void populateQuestLockedClues()
	{
		QUEST_LOCKED_CLUE_ITEM_IDS.clear();
		QUEST_LOCKED_CLUE_LOCATIONS.clear();
		QUEST_LOCKED_CLUE_TEXTS.clear();

		QUEST_LOCKED_CLUE_ITEM_IDS.addAll(Arrays.asList(
		));

		QUEST_LOCKED_CLUE_LOCATIONS.addAll(Arrays.asList(
		));

		QUEST_LOCKED_CLUE_TEXTS.addAll(Arrays.asList(
		));
	}

	@Inject
	private ItemRequirementHelper itemRequirementHelper;

	public boolean requiresInaccessibleQuest(ClueScroll clue, ClueScrollPlugin clueScrollPlugin)
	{
		if (clue instanceof LocationClueScroll)
		{
			LocationClueScroll locationClue = (LocationClueScroll) clue;
			WorldPoint location = locationClue.getLocation(clueScrollPlugin);
			if (location != null && QUEST_LOCKED_CLUE_LOCATIONS.contains(location))
			{
				return true;
			}
		}

		if (clue instanceof EmoteClue)
		{
			EmoteClue emoteClue = (EmoteClue) clue;
			var requirements = emoteClue.getItemRequirements();
			if (requirements != null)
			{
				for (var req : requirements)
				{
					if (req instanceof SingleItemRequirement)
					{
						int itemId = itemRequirementHelper.getItemIdFromSingleRequirement((SingleItemRequirement) req);
						if (itemId != -1 && QUEST_LOCKED_CLUE_ITEM_IDS.contains(itemId))
						{
							return true;
						}
					}
				}
			}
			String text = emoteClue.getText();
			if (text != null && containsQuestLockedText(text))
			{
				return true;
			}
		}

		if (clue instanceof CrypticClue)
		{
			CrypticClue crypticClue = (CrypticClue) clue;
			String text = crypticClue.getText();
			if (text != null && containsQuestLockedText(text))
			{
				return true;
			}
			String solution = crypticClue.getSolution(clueScrollPlugin);
			if (solution != null && containsQuestLockedText(solution))
			{
				return true;
			}
		}

		if (clue instanceof SkillChallengeClue)
		{
			SkillChallengeClue skillChallenge = (SkillChallengeClue) clue;
			String challenge = skillChallenge.getChallenge();
			if (challenge != null && containsQuestLockedText(challenge))
			{
				return true;
			}
		}

		return false;
	}

	private boolean containsQuestLockedText(String text)
	{
		if (text == null)
		{
			return false;
		}

		String lowerText = text.toLowerCase();
		for (String questLockedText : QUEST_LOCKED_CLUE_TEXTS)
		{
			if (lowerText.contains(questLockedText.toLowerCase()))
			{
				return true;
			}
		}

		Set<String> mentionedQuests = extractQuestNames(text);
		for (String questName : mentionedQuests)
		{
			if (!SkillerCompletableQuests.isSkillerCompletable(questName))
			{
				return true;
			}
		}

		return false;
	}

	private Set<String> extractQuestNames(String text)
	{
		Set<String> questNames = new HashSet<>();
		if (text == null)
		{
			return questNames;
		}

		Pattern questPattern = Pattern.compile("(?:after|during|from|in|requires?|completing?|finished?|done with|\\()\\s*(?:the\\s+)?([A-Z][a-zA-Z&'\\s]+?)\\s*(?:quest|Quest|\\))", Pattern.CASE_INSENSITIVE);
		Matcher matcher = questPattern.matcher(text);
		while (matcher.find())
		{
			String questName = normalizeQuestName(matcher.group(1).trim());
			if (questName.length() > 2 && !questName.equalsIgnoreCase("the"))
			{
				questNames.add(questName);
			}
		}

		Pattern questPattern2 = Pattern.compile("([A-Z][a-zA-Z&'\\s]+?)\\s+(?:quest|Quest)(?:\\s+required|\\s+needed|\\s+unlocked)?", Pattern.CASE_INSENSITIVE);
		Matcher matcher2 = questPattern2.matcher(text);
		while (matcher2.find())
		{
			String questName = normalizeQuestName(matcher2.group(1).trim());
			if (questName.length() > 2 && !questName.equalsIgnoreCase("the"))
			{
				questNames.add(questName);
			}
		}

		return questNames;
	}

	private String normalizeQuestName(String questName)
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
		return questName;
	}
}

