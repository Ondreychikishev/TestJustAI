#Тестовый сценарий на момент обучения
theme: /edu02

    state: Купить диван || module=true
        q!: * {[как*] * $buy * (диван*)} 
        a: Из какого Вы города?
            
        state: Local CatchAll
            q: *
            a: Ошибка.
            go!: ..
        
        state: Город выбран
            q: * $ptrnCity *
            script:
                getcity();
                log("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + $session.ptrnCity);
                $response.city = $session.ptrnCity;
            a:  Какой диван вас интересует? Диван-кровать, угловой или прямой?
            buttons: 
                "Диван-кровать"
                "Угловой диван"
                "Прямой диван"
        
            state: Тип дивана
                q: * $coachType *
                a: Хорошо, на какую сумму?
                go!: /Сумма
            
        state: Сумма
            q: * $NumberDigit *
            script: 
                log("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + toPrettyString($parseTree));
                $temp.coachSum = $parseTree._NumberDigit;
            if: $temp.coachSum <= 10000
                a: К сожалению, в нашем магазине нет диванов на такую сумму.
            else:
                go!: /Выбор магазина
                            
        state: Выбор магазина
            a: В каком магазине вы хотели бы приобрести диван?
            
            state: Office
                q: * $shopsName *
                script:
                    $temp.address = $parseTree._shopsName.address;
                a: Тогда приезжайте по адресу {{$temp.address}}!