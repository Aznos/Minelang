//secret word: hello
place sack in slot 1 contains [pumpkin_stem, iron_bars, brick_stairs, brick_stairs, waterlily]

//Constants
place stone in slot 9 // 1
place air in slot 8 // 0

//"guess word: "
ask [melon_block, brewing_stand, iron_bars, nether_wart, nether_wart, deadbush, end_portal, waterlily, nether_brick_stairs, red_mushroom_block, crafting_table, deadbush] in slot 2

//compare lengths
length slot 1 in slot 3
length slot 2 in slot 4

redstone slot 3 bedrock slot 4 then
    place air in slot 5
    travel slot 6 from slot 9 to slot 3 do
        redstone harvest slot 1 at slot 6 bedrock harvest slot 2 at slot 6 then
            //match: do nothing
        else
            //mismatch: mismatches +1
            craft slot 5 with slot 9 in slot 5
        end
    end

    //mismatches = 0 = win
    redstone slot 5 bedrock air then
        say brew [end_portal, melon_stem, mycelium] as string
    else
        say brew [brick_stairs, waterlily, nether_wart, iron_bars] as string
    end
else
    say brew [brick_stairs, waterlily, nether_wart, iron_bars] as string
end