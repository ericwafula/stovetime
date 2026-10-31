#!/usr/bin/env bash
# Links .ai/skills/<name> into .claude/skills/<name> and .ai/agents/<name>.md
# into .claude/agents/<name>.md so Claude Code picks them up. Safe to re-run.
set -euo pipefail

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$repo_root"

link_all() {
  local dest="$1" prefix="$2"; shift 2
  mkdir -p "$dest"
  for src in "$@"; do
    [[ -e "$src" ]] || continue
    [[ "$(basename "$src")" == "README.md" ]] && continue
    local name link target
    name="$(basename "$src")"
    link="$dest/$name"
    target="$prefix/$name"
    if [[ -L "$link" ]]; then
      [[ "$(readlink "$link")" == "$target" ]] || { rm "$link"; ln -s "$target" "$link"; }
    elif [[ -e "$link" ]]; then
      echo "warn: $link exists and is not our symlink; left untouched" >&2
    else
      ln -s "$target" "$link"
    fi
  done
}

link_all .claude/skills ../../.ai/skills .ai/skills/*/
link_all .claude/agents ../../.ai/agents .ai/agents/*.md

echo "ok: $(ls -d .ai/skills/*/ | wc -l | tr -d ' ') skills, $(ls .ai/agents/*.md | grep -v README | wc -l | tr -d ' ') agents linked"
