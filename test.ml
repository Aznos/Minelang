place cobblestone in slot 1 // slot 1 = 4
place dirt in slot 2 // slot 2 = 3

redstone slot 1 bedrock slot 2 then // if slot 1 == slot 2 then
    say piston to string // print "["
else
    say lit_pumpkin to string // print "!"
end
say flowing_lava to string // newline

place stone in slot 1 // slot 1 = 1
mine slot 2 tnt air do // if slot 2 != 0
    shear slot 1 from slot 2 into slot 2 // slot 2 = slot 2 - slot 1
end
say slot 2 // after loop, slot 2 = 0
say flowing_lava to string // newline

place planks in slot 3 // slot 3 = 5
smelt slot 3 times do // repeat 5 times
    craft slot 1 with slot 1 into slot 1 // double slot 1 each time
end
say slot 1 // after loop, slot 1 = 32
say flowing_lava to string // newline

place wooden_pressure_plate in slot 1 // slot 1 = 72 (H)
place redstone_torch in slot 2 // slot 2 = 76 (L)
travel slot 9 from slot 1 to slot 2 do // loop from 72 to 76
    say slot 9 to string // HIJKL
end