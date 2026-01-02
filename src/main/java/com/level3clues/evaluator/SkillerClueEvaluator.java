package com.level3clues.evaluator;

import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.client.plugins.cluescrolls.ClueScrollPlugin;
import net.runelite.client.plugins.cluescrolls.clues.ClueScroll;
import net.runelite.client.plugins.cluescrolls.clues.CipherClue;
import net.runelite.client.plugins.cluescrolls.clues.CrypticClue;
import net.runelite.client.plugins.cluescrolls.clues.EmoteClue;
import net.runelite.client.plugins.cluescrolls.clues.FaloTheBardClue;
import net.runelite.client.plugins.cluescrolls.clues.SkillChallengeClue;
import net.runelite.client.plugins.cluescrolls.clues.ThreeStepCrypticClue;
import com.level3clues.evaluator.combat.CombatItemDetector;
import com.level3clues.evaluator.location.LocationValidator;
import com.level3clues.evaluator.quest.QuestRequirementChecker;

@Singleton
public class SkillerClueEvaluator
{
	@Inject
	private LocationValidator locationValidator;

	@Inject
	private QuestRequirementChecker questRequirementChecker;

	@Inject
	private CombatItemDetector combatItemDetector;

	public boolean isClueGoodForSkiller(ClueScroll clue, ClueScrollPlugin clueScrollPlugin)
	{
		if (clue == null)
		{
			return true;
		}

		if (locationValidator.isInMorytania(clue, clueScrollPlugin))
		{
			return false;
		}

		if (questRequirementChecker.requiresInaccessibleQuest(clue, clueScrollPlugin))
		{
			return false;
		}

		if (clue instanceof SkillChallengeClue)
		{
			SkillChallengeClue skillChallenge = (SkillChallengeClue) clue;
			String challenge = skillChallenge.getChallenge();
			if (challenge != null && combatItemDetector.requiresCombat(challenge))
			{
				return false;
			}
			String rawChallenge = skillChallenge.getRawChallenge();
			if (rawChallenge != null && combatItemDetector.requiresCombat(rawChallenge))
			{
				return false;
			}
			var requirements = skillChallenge.getItemRequirements();
			if (combatItemDetector.hasCombatRequirements(requirements))
			{
				return false;
			}
		}

		if (clue instanceof EmoteClue)
		{
			EmoteClue emoteClue = (EmoteClue) clue;
			var requirements = emoteClue.getItemRequirements();
			if (requirements != null && requirements.length > 0 && combatItemDetector.hasCombatRequirements(requirements))
			{
				return false;
			}
		}

		String clueText = getClueText(clue, clueScrollPlugin);
		if (clueText != null && combatItemDetector.requiresCombat(clueText))
		{
			return false;
		}

		return true;
	}

	private String getClueText(ClueScroll clue, ClueScrollPlugin clueScrollPlugin)
	{
		if (clue instanceof CrypticClue)
		{
			return ((CrypticClue) clue).getText();
		}

		if (clue instanceof CipherClue)
		{
			return ((CipherClue) clue).getText();
		}

		if (clue instanceof FaloTheBardClue)
		{
			return ((FaloTheBardClue) clue).getText();
		}

		if (clue instanceof ThreeStepCrypticClue)
		{
			return ((ThreeStepCrypticClue) clue).getText();
		}

		if (clue instanceof EmoteClue)
		{
			return ((EmoteClue) clue).getText();
		}

		return null;
	}
}

