place stone in slot 9 // slot 9 = secret number (1)

command checkGuess do
    redstone slot 1 bedrock slot 2 then // if equal, bedrock means ==
        say brew [end_portal, melon_stem, mycelium] as string // "win"
        say brew [flowing_lava] as string // newline
        scribe slot 1 in slot 1 // copy 1 into slot 1 as return flag
    else
        redstone slot 1 tnt slot 2 then // if not secret (tnt means !=)
            say brew [mycelium, waterlily] as string // no
            say brew [flowing_lava] as string // newline
        end

        place air in slot 1 // slot 1 = 0
    end
end

// game loop
place air in slot 8 // done = 0
mine slot 8 bedrock air do // while loop, while done is 0
    say brew [melon_block, brewing_stand, iron_bars, nether_wart, nether_wart, crafting_table] as string // "guess"
    say brew [flowing_lava] as string // newline
    ask stone in slot 5 // ask for guess, set slot 5 to guess

    activate checkGuess with [slot 5, slot 9] in slot [slot 8] // call checkGuess(guess, secret) -> done
end