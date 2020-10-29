patterns:
    $shopsName = $entity<bunit> || converter = $converters.BunitCSVConverter
    $ptrnCity = $entity<city> || converter = $converters.CityCSVConverter
    $coachType = (диван-кроват*|диван с кроват*|угловой|прямой)
    $buy = (*купить/покуп*/приобрести/заказать)
    $NumberDigit = $regexp<\d+>
    