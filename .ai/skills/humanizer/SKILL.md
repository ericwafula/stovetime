---
name: humanizer
description: Rewrite text so it reads like a person wrote it — strip AI tells from prose, make Stovetime UI copy short, warm and plain for beginner home cooks, or make commit/PR/review text sound like a real teammate. Voice only; format comes from commit-convention and the github skills.
when_to_use: User asks to humanize, de-robotify, "make this sound less AI", reword UI strings, recipe steps or error messages, or clean up a PR description, commit body, README section or review reply.
---

# Skill: humanizer

Rewrite only the prose. Keep meaning, claims, numbers, links, code and identifiers exactly. Never invent facts to sound more natural. If unsure which mode applies, ask. The default is **De-AI-ify**.

## Mode 1: De-AI-ify (general prose, README, docs)

- Cut filler openers and closers ("It's worth noting…", "In today's world…", "I hope this helps").
- Use plain words: delve → look at, leverage/utilize → use, facilitate → help, robust/seamless → say the concrete thing.
- No "It's not just X, it's Y" cadence, no lists padded to three, and em dashes only rarely.
- Don't over-hedge ("may potentially"). State it.
- Vary sentence length and delete sentences that carry no information.
- Drop summary paragraphs that repeat what was just said.

## Mode 2: UI copy (strings.xml, sheets, errors, empty states, cook steps)

Write for someone who is new to cooking, standing at the stove with one free hand.

- Short, warm and plain. A friend in the kitchen, not a chef and not a cheerleader.
- Say what happened and what to do next: "Couldn't load this recipe. Check your connection and try again." rather than "An unexpected error occurred."
- No blame ("You failed to…"), no tech jargon (HTTP, API, sync, null), and no kitchen jargon without a plain word next to it ("Simmer: small bubbles, not a rolling boil").
- Cook steps start with the action and fit in a glance: "Lower the heat and stir for 2 minutes."
- Celebrate real progress, briefly: "Nice one. +30 XP" on the finish screen. Save exclamation marks for those moments; never in errors, warnings or instructions.
- Streak and XP copy encourages, never guilts: "Cook once this week to keep your streak" rather than "Don't lose your streak!"
- Permission rationale explains the benefit first: "Allow notifications so your timer can ring when the eggs are ready."
- Times, amounts and XP use the format the app shows ("15 min", "6:30", "+30 XP"). Never state a time, serving size or fact the recipe data doesn't have; TheMealDB doesn't give every recipe a cook time.
- Copy must match the code path that shows it. If you can't see the trigger, flag it instead of guessing.

## Mode 3: Commit, PR and review text

- Follow `commit-convention` / `github-create-pr` for format. This mode fixes voice only.
- Lead with what changed and why. Cut "This PR aims to…", empty headings and padded bullets.
- Don't add a commit body just to look thorough.
- Review replies are direct and warm, not corporate.
- **Never** add AI co-author trailers, "generated with" lines or tool names anywhere.

## Output

Return only the rewritten text unless the user asks for a before/after. Keep it no longer than the original unless asked to expand. If the original is factually wrong, say so separately instead of quietly changing the claim.
