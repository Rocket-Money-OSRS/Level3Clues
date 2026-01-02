package com.level3clues;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.client.plugins.cluescrolls.ClueScrollPlugin;
import net.runelite.client.plugins.cluescrolls.clues.ClueScroll;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import com.level3clues.evaluator.SkillerClueEvaluator;

public class Level3CluesOverlay extends OverlayPanel
{
	private final Level3CluesPlugin plugin;
	private final SkillerClueEvaluator evaluator;

	@Inject
	private Level3CluesOverlay(Level3CluesPlugin plugin, SkillerClueEvaluator evaluator)
	{
		super(plugin);
		this.plugin = plugin;
		this.evaluator = evaluator;
		setPosition(OverlayPosition.TOP_LEFT);
		setPriority(PRIORITY_HIGH);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!plugin.getConfig().showIndicator())
		{
			return null;
		}

		ClueScrollPlugin clueScrollPlugin = plugin.getClueScrollPlugin();
		if (clueScrollPlugin == null)
		{
			return null;
		}

		ClueScroll clue = clueScrollPlugin.getClue();
		if (clue == null)
		{
			return null;
		}

		boolean isGood = evaluator.isClueGoodForSkiller(clue, clueScrollPlugin);

		panelComponent.getChildren().add(LineComponent.builder()
			.left("lvl 3: " + (isGood ? "Good" : "Skip"))
			.leftColor(isGood ? Color.GREEN : Color.RED)
			.build());

		return super.render(graphics);
	}
}


