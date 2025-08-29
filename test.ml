command addmul do
    craft slot 1 with slot 2 in slot 3
    smith slot 1 with slot 2 in slot 4

    scribe slot 3 in slot 1
    scribe slot 4 in slot 2
end

place stone in slot 9
place cobblestone in slot 10
activate addmul with [slot 9, slot 10] in slot [slot 5, slot 6]

say slot 5
say slot 6