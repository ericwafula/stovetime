# Phase 3 — Shopping list

**Order:** 3.1 → 3.2 → 3.3 → 3.4

## 3.1 Add a recipe to the list

**Size:** S · **Depends on:** 2.3

### Description
From a recipe, add its ingredients to the shopping list, unticking the ones you already have.

### Acceptance Criteria
- [ ] "Add to shopping list" shows the ingredients, all ticked, scaled to the chosen servings
- [ ] Unticked ingredients aren't added
- [ ] Each item remembers which recipe it came from
- [ ] Tests cover: only ticked items are added, scaled amounts are used

---

## 3.2 Merge and group by aisle

**Size:** M · **Depends on:** 3.1

### Description
Two recipes that both need onions should give one "onions" line, and the list should follow the shop: produce, meat, dairy, dry goods.

### Acceptance Criteria
- [ ] Items with the same name and a compatible unit are merged and their amounts added
- [ ] Items are grouped by aisle, with a fallback "Other" group
- [ ] Moving an item to another aisle is remembered for next time
- [ ] Tests cover: merging compatible units, keeping incompatible ones separate, aisle memory

---

## 3.3 Tick off and edit

**Size:** S · **Depends on:** 3.2

### Description
Use the list in the shop: tick items off, add your own, and clear what's done.

### Acceptance Criteria
- [ ] Ticking moves an item to a "Got it" section at the bottom
- [ ] Your own items can be added in one line ("2 kg sugar")
- [ ] "Clear done" removes ticked items, with undo
- [ ] Tests cover: tick and untick, quick-add parsing, clear with undo

---

## 3.4 Share the list

**Size:** S · **Depends on:** 3.3

### Description
Send the list to whoever is going to the shop, as plain text grouped by aisle.

### Acceptance Criteria
- [ ] Share sends the unticked items as text, grouped by aisle
- [ ] The text reads well in WhatsApp and SMS
- [ ] Tests cover: text format for a list with several aisles
