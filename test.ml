// hello in an array
place sack in slot 1 contains [pumpkin_stem, iron_bars, brick_stairs, brick_stairs, waterlily]

length slot 1 into slot 2 // slot 2 = 5 (5 characters in hello)
place stone in slot 4 // slot 4 = value of 1 (start of index)

// i (slot3) goes from slot4 (1) to slot2 (length)
// harvest slot1 at i -> character
travel slot 3 from slot 4 to slot 2 do
    say harvest slot 1 at slot 3 to string
end