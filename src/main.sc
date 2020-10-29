# -----------------------------------------------------------------------------
# ------------------------ Подключение слот-филинга ---------------------------
# -----------------------------------------------------------------------------

#подключаем модуль слот-филлинга
require: slotfilling/slotFilling.sc
    module = sys.zb-common

# ----------------------------------------------------------------------------
# ------------------------ Подключение справочников --------------------------
# ----------------------------------------------------------------------------

#Справочник функций javascript
require: scripts/functions.js

#Справочник цифр
require: number/number.sc
    module = common

#Справочник бизнес-юнитов
require: dictionaries/bunits.csv
    name = bunit
    var = $bunit

#Справочник городов
require: dictionaries/cities-ru.csv
    name = city
    var = $city

#Справочник паттернов
require: scenarios/patterns.sc

# ----------------------------------------------------------------------------
# -------------------------- Подключение сценариев ---------------------------
# ----------------------------------------------------------------------------

require: scenarios/S001-support-FAQ-hr.sc

#require: scenarios/archive/operators.sc
#require: scenarios/archive/edu02.sc

# -----------------------------------------------------------------------------
# --------------------------- Скрипты-обработчики -----------------------------
# -----------------------------------------------------------------------------

init:

# Запрос переопределение всех ошибок

#    bind("onAnyError", function($context) {
#        $jsapi.context().session.OperatorTransFlag = true;                      //# Если возникла любая ошибка, то разрешено переключить на оператора сразу (пользоватиельская переменная)
#        $reactions.answer('К сожалению, по техническим причинам я не смогу сейчас помочь. Попробуйте переключиться на оператора, отправив фразу "[Переключить на оператора]" в чат или обратитесь позже.');
#        $reactions.inlineButtons({text: "Переключить на оператора", transition: "/Operator"})
#    });

        # или <a href="https://hoff.ru/feedback" target="_blank">отправить email в рубрику «Напишите нам» на сайте hoff.ru</a>
        # $reactions.inlineButtons({text: "Форма обращения по Email", url: "https://hoff.ru/feedback"});

# Глобальные таймауты при неответе клиента (120 = 2 мин | 180 = 3 мин | 300 сек = 5 мин)
# метод .startsWith(); проверяет текст, начиная с начала строки, поэтому все вложенные стейты тоже буду ему сответствовать.  
 

    #Только, когда находимся в любом стейте, кроме уточнения вопросов, прощания, оценки или закрытия сессии
    bind("postProcess", function() {
        if (!$jsapi.context().currentState.startsWith("/ВопросыОстались")                               //# кроме стейта, уточняющего наличие доп вопросов у клиента
        &&  !$jsapi.context().currentState.startsWith("/ВопросПереформулируйте")                        //# кроме стейта, с просьбой переформулировать вопрос
        &&  !$jsapi.context().currentState.startsWith("/ВопросУточните")                                //# кроме стейта, с просьбой уточнить вопрос
        &&  !$jsapi.context().currentState.startsWith("/Начало")                                        //# кроме стейта, с приветствием
        &&  !$jsapi.context().currentState.startsWith("/Operator")                                      //# кроме стейта, переводящего на стейт Switch
        &&  !$jsapi.context().currentState.startsWith("/CloseSessionByTimeout")                         //# кроме стейта, переводящего на стейт Bye по таймауту
        &&  !$jsapi.context().currentState.startsWith("/Bye")                                           //# кроме стейта, переводящего на оценку удовлетворенности
        &&  !$jsapi.context().currentState.startsWith("/Оценка")                                        //# кроме стейта, уточняющего уровень удовлетовренности у клиента (также на этот стейт попадаем, когда оператор закрывает чат)
        &&  !$jsapi.context().currentState.startsWith("/CloseSession")                                  //# кроме стейта, закрывающего сессию
        &&  !testMode())
            {
                $reactions.timeout({interval: 120, targetState: '/ВопросыОстались'});
            }
    });

    #Только, когда находимся в стейте, с приветствием
    bind("postProcess", function() {
        if ($jsapi.context().currentState.startsWith("/Начало")                                         //# когда в стейте, с приветствием
        &&  !testMode())
            {
                $reactions.timeout({interval: 600, targetState: '/CloseSession'});
            }
    });

    #Только, когда находимся в стейте, уточняющем не сотались ли еще вопросы
    bind("postProcess", function() {
        if ($jsapi.context().currentState.startsWith("/ВопросыОстались")                                //# когда в стейте, уточняющем наличие доп вопросов у клиента
        ||  $jsapi.context().currentState.startsWith("/ВопросПереформулируйте")                         //# когда в стейте, с просьбой переформулировать вопрос
        ||  $jsapi.context().currentState.startsWith("/ВопросУточните")                                 //# когда в стейте, с просьбой уточнить вопрос
        &&  !testMode())
            {
                $reactions.timeout({interval: 180, targetState: '/CloseSessionByTimeout'});
            }
    });

    #Только, когда находимся в стейтах прощания или оценки
    bind("postProcess", function() {
        if ($jsapi.context().currentState.startsWith("/Bye")                                            //# когда в стейте, переводящем на оценку удовлетворенности
        ||  $jsapi.context().currentState.startsWith("/Оценка")                                         //# когда в стейте, уточняющем уровень удовлетовренности у клиента (также на этот стейт попадаем, когда оператор закрывает чат)
        &&  !testMode())
            {
                $reactions.timeout({interval: 300, targetState: '/CloseSession'});
            }
    });

# Препроцесс-обработчик, игнорирующий глобальные интенты в стейте с отзывом, чтобы не тупил.

    bind("preProcess", function(ctx) {
        var currState = ctx.currentState;
        var currContx = ctx.contextPath;
        var nextState = ctx.temp.classifierTargetState;
        if (currContx == "/Оценка"
        &&  currContx != "/CloseSession"                                                                //# это нужно, чтобы не конфликтовало с таймаутом в стейте с оценкой
        &&  nextState != "/Оценка/ОценкаКомментарий"
        &&  nextState != "/CloseSession")                                                               //# это нужно, чтобы не конфликтовало с таймаутом в стейте с оценкой
            {
                ctx.temp.classifierTargetState = "/Оценка/ОценкаКомментарий";
                $reactions.transition("/Оценка/ОценкаКомментарий");
            }
        if (currContx == "/Оценка/ОценкаКомментарий"
        &&  currContx != "/CloseSession"                                                                //# это нужно, чтобы не конфликтовало с таймаутом в стейте с оценкой
        &&  nextState != "/Оценка/ОценкаКомментарий/ОценкаЗакрытие"
        &&  nextState != "/CloseSession")                                                               //# это нужно, чтобы не конфликтовало с таймаутом в стейте с оценкой
            {
                ctx.temp.classifierTargetState = "/Оценка/ОценкаКомментарий/ОценкаЗакрытие";
                $reactions.transition("/Оценка/ОценкаКомментарий/ОценкаЗакрытие");
            }
    });

# Препроцсс-обработчик, который отлавливает стейт, с которого был переход на оператора

    bind("preProcess", function(ctx) {
        var currState = ctx.currentState;
        var currContx = ctx.contextPath;
        var nextState = ctx.temp.classifierTargetState;
        if (currContx != "/Operator"
        &&  currContx != "/CatchAll"
        &&  currContx != "/ВопросыОстались")
            {
                if (nextState == "/Operator"
                ||  nextState == "/CatchAll"
                ||  nextState == "/ВопросыОстались")
                    {
                        ctx.session.smartSwitchLastState = currContx;
                    }
            }
        //# ниже приведены фразы прикрепляемые к каждому ответу бота за счет этого препроцесса.
        //# на момент их раскоментирования для успешного деплоя даже в чат-виджет разработчика необходимо отключать тест кейсы.
        //$reactions.answer("1-CS: "+currState);
        //$reactions.answer("2-CC: "+currContx);
        //$reactions.answer("3-NS: "+nextState);
        //$reactions.answer("4-LS: "+ctx.session.smartSwitchLastState);
    });

# Переопределение ответа бота, когда он начинает повторяться

    bind("postProcess", function($context) {
        var currentAnswer = $context.response.replies.reduce(function(allAnswers, reply) {
            allAnswers += reply.type === "text" ? reply.text : "";  
            return allAnswers;
        },"");
        if ($context.session.lastAnswer === currentAnswer && !testMode()) {
            $context.response.replies = [
            {
                "type":"text",
                "text":"Потребуется помощь оператора."
            }
            ];
            $reactions.transition({value: "/Operator", deferred: false});       //# deferred: false = go!:
        }
        $context.session.lastAnswer = currentAnswer;
    });

#    bind("postProcess", function($context) {
#        var currentAnswer = $context.response.replies.reduce(function(allAnswers, reply) {
#            allAnswers += reply.type === "text" ? reply.text : "";  
#            return allAnswers;
#        },"");
#        if ($context.session.lastAnswer === currentAnswer && !testMode()) {
#            $context.response.replies = [
#            {
#                "type":"text",
#                "text":"..."
#            }
#            ];
#            $reactions.transition("/Operator");
#        }
#        $context.session.lastAnswer = currentAnswer;
#    });

# Переопределение последовательнсоти матчера

    bind("selectNLUResult", function(ctx) {                                     //# задаем обработчик для фазы `selectNLUResult`
            log(ctx.nluResults);                                                //# выводим результаты в лог
            //# используем результат от интентов
            if (ctx.nluResults.intents.length > 0)
                {
                    ctx.nluResults.selected = ctx.nluResults.intents[0];
                    return;
                }
            //# если результата от интентов нет, используем паттерны
            if (ctx.nluResults.patterns.length > 0)
                {
                    ctx.nluResults.selected = ctx.nluResults.patterns[0];
                    return;
                }
            //# если результата от интентов и паттернов нет, используем примеры
            if (ctx.nluResults.examples.length > 0)
                {
                    ctx.nluResults.selected = ctx.nluResults.examples[0];                           
                }
    });

# Глобальные конвертеры

    if (!$global.$converters) {
        $global.$converters = {};
    }
    $global.$converters
        .BunitCSVConverter = function(parseTree) {
            var id = parseTree.bunit[0].value;
            return $bunit[id].value;
        };
    $global.$converters
        .CityCSVConverter = function(parseTree) {
            var id = parseTree.city[0].value;
            return $city[id].value;
        };

# Сбор истории переписки для команды switch
# Запрос препроцесс собирает реплики клиента, а постпроцесс — реплики бота
# В переменную $context.session.chatHistorySTAT нарастающим итогом записывается значение полного пути стейта, из которого взят ответ бота

    $jsapi.bind({
        type: "preProcess",
        name: "savingVisitorChatHistory",
        path: "/",
        handler: function($context) {
            $context.client.chatHistory = $context.client.chatHistory || [];
            var chatHistory = $context.client.chatHistory;
            if ($context.request.query) {
                chatHistory.push({type: "Клиент", state: "", text: $context.request.query});
            }
            chatHistory.splice(0, chatHistory.length - 10);
        }
    });
    $jsapi.bind({
        type: "postProcess",
        name: "savingBotChatHistory",
        path: "/",
        handler: function($context) {
            $context.client.chatHistory = $context.client.chatHistory || [];
            var chatHistory = $context.client.chatHistory;
            if ($context.response.replies) {
                $context.response.replies
                    .filter(function(val) { return val.type === "text"; })
                    .forEach(function(val) { chatHistory.push({ type: "BOT", state: "( Тема: "+$context.currentState+" )", text: val.text }); });
            }
            chatHistory.splice(0, chatHistory.length - 10);
            $context.session.chatHistorySTAT += $context.currentState;
        }
    });

# Отправка дополнительной аналитики
# https://help.just-ai.com/#/docs/ru/analytics/custom_fields

    bind("postProcess", function($ctx) {
        $ctx.response.client = $ctx.client;
        $ctx.response.session = $ctx.session;
        $ctx.response.temp = $ctx.temp;
        if ($jsapi.context().currentState.startsWith("/S001/")
        ||  $jsapi.context().currentState.startsWith("/CatchAll")
        ||  $jsapi.context().currentState.startsWith("/Operator"))
            {
                $ctx.session.analyticsCase = true; //# фраза пригодна для аналитики (авторазметка)
            }
        else
            {
                $ctx.session.analyticsCase = false; //# фраза не пригодна для аналитики (авторазметка)
            };
        //# Примечание: для analyticsCase обязателно использование либо true, либо false, исключая null и остальные пустые значения, чтобы удобно было (вручную в эксель через функцияю ЕПУСТО) решать проблему разметки фраз, на которые отвечал оператор
    });

# -----------------------------------------------------------------------------
# ----------------------------- Главный сценарий ------------------------------
# -----------------------------------------------------------------------------

theme: /

#    state: LengthLimit
#        event!: lengthLimit
#        a: попали на ивент lengthLimit

#    state: TimeLimit
#        event!: timeLimit
#        a: попали на ивент timeLimit 

    state: TestError
        q!: ошибка перехода
        go!: /dsdsa

    
    state: Начало
        intent!: /Общие/MainMenu
        q!: ((*start* *) | *старт | *ыефке | *cnfhn)
        script:
            //# обрабатываем данные пользователя
            updateClientInfo();
        if: !((($client || {}).user || {}).name)
            a: Привет! Меня зовут Ииигорь. Я виртуальный помощник сотрудников Hoff.
        else:
            a: Привет, {{ $client.user.name }}! Меня зовут Ииигорь. Я виртуальный помощник сотрудников Hoff.
        a:  Могу подсказать:
            <ul>
                <li>[справочник сотрудников];</li>
                <li>[справочник магазинов];</li>
                <li>[отпуска];</li>
                <li>[все навыки].</li>
            </ul>
            По другим кадровым вопросам я подключаю специалистов по графику (c 10 до 19 МСК, пн-пт).
            По техническим вопросам:
            <ul>
                <li>звоните в [helpdesk] на внутренний номер 333+1 или пишите письмо на почту <a href="mailto:helpdesk@hoff.ru" target="_blank">helpdesk@hoff.ru</a></li>
            </ul>
            Спрашивайте, не стесняйтесь... 😉        
#        a:  Могу подсказать:
#            <ul>
#                <li>даты выплат [зарплаты] и [премии];</li>
#                <li>[коды магазинов], дирекций, прочих БЮ и кто их руководитель;</li>
#                <li>[расписание автобуса];</li>
#                <li>[Все навыки].</li>
                #<li><a href="http://confluence.kifr-ru.local:8090/pages/viewpage.action?pageId=46041071" target="_blank">Все навыки</a>.</li>
#            </ul>
#            По другим кадровым вопросам я подключаю специалистов по графику (c 10 до 19 МСК, пн-пт).
#            По техническим вопросам:
#            <ul>
#                <li>звоните в [helpdesk] на внутренний номер 333+1 или пишите письмо на почту <a href="mailto:helpdesk@hoff.ru" target="_blank">helpdesk@hoff.ru</a></li>
#            </ul>
#            Спрашивайте, не стесняйтесь... 😉
        random:
            a:  Чем могу помочь?
            a:  Какой ваш вопрос?
            a:  Что бы вы хотели обсудить?
            a:  В чём ваш вопрос?
        
    state: ЧтоТыУмеешь || noContext=true
        intent!: /Общие/ЧтоТыУмеешь
        script:
            sleep();
        a:  Со всеми вопросами, на которые я отвечаю, также можно ознакомиться по ссылке:
            <ul><li><a href="http://confluence.kifr-ru.local:8090/pages/viewpage.action?pageId=46041071" target="_blank">http://confluence.kifr-ru.local:8090/pages/viewpage.action?pageId=46041071</a></li></ul>
            Спрашивайте, не стесняйтесь...
        go!: /S001/К00

    state: helpdesk || noContext=true
        intent!: /Общие/Helpdesk
        script:
            sleep();
        a:  В службе технической поддержки Hoff (helpdesk):
            <ul>
                <li>работают квалифицированные специалисты, только они обладают сверхспособностью договариваться с техникой, чтобы она снова заработала, даже казалось бы в безнадёжных ситуациях.</li>
            </ul>
            При проблемах с техникой:
            <ul>
                <li>смело звоните в helpdesk на внутренний номер 333+1;</li>
                <li>или пишите письмо на почту <a href="mailto:helpdesk@hoff.ru" target="_blank">helpdesk@hoff.ru</a></li>
            </ul>
            В обоих случаях:
            <ul>
                <li>будьте готовы указать ваш <b>табельный номер</b>, а также имя компьютера (на котором возникла проблема) или его IP адрес;</li>
                <li>наизусть достаточно помнить только табельный номер, остальные данные чаще всего выведены на рабочий стол компьютера справа над часами и датой (Host Name - это имя компьютера, IP Addres - это IP адрес компьютера), либо приклены стикером на монитор.</li>
            </ul>        
            Будьте вежливы и корректны:
            <ul>
                <li>помните, что специалист, принимающий вашу заявку и специалист, занимающийся ее решением могут оказаться разными людьми, да ещё и работающими в разных отделах, а то и в разных городах или даже частях нашей страны.</li>
            </ul>
            Кратко и точно опишите суть проблемы:
            <ul>
                <li>работа специалиста в линии связана с определенной долей стресса, когда за короткий промежуток времени необходимо принять ответственное решение или правильно оформить вашу заявку и отправить её по назначению, поэтому хорошим тоном с вашей стороны будет как можно точнее описать суть проблемы, нежели начинать диалог с высказывания недовольства по поводу сложившейся ситуации, даже при повторном обращении.</li>
            </ul>
            Исключите категоричность:
            <ul>
                <li>подавляющая часть процессов в работе тех. поддержки уже отлажена и проверена на значимом количестве ситуаций с учётом всех деталей, поэтому бессмысленно настаивать на своём видении того, как должно быть, вы лишь в праве предложить свой вариант при желании, но не преподносить его как единственно верный.</li>
            </ul>
                    
# ------------------- Действие, если бот непонимает клиента -------------------
    
    #отлов всех нераспознанных фраз клиента
    state: CatchAll || noContext=true
        event!: noMatch
        script:
            //# Активация встроенной функции транслитерацит сообщений, не содержащих ни одного кириллического символа.
            var text = $parseTree.text;
            $temp.fixedText = $nlp.fixKeyboardLayout(text);
            if ($temp.fixedText) {
                var matchResults = $nlp.match($temp.fixedText, "/");  
                $parseTree = matchResults.parseTree;
                $temp.nextState = matchResults.targetState;
                $reactions.answer('Возможно, вы имели ввиду:<ul><li>{{$temp.fixedText}}</li></ul>');
                $reactions.transition({value: $temp.nextState, deferred: false});               //# deferred: false = go!:
            } else {
                $session.noMatchCount = $session.noMatchCount || 0; ++$session.noMatchCount;    //# считает количество нераспознанных сообщений (пользовательская переменная)
                $reactions.transition({value: "/Operator", deferred: false});                   //# deferred: false = go!:
            };

# ----------------------- Уведомление о смене контекста -----------------------

    state: ContextChangeAcception
        script:
            $response.replies = [];
        a:  — ВНИМАНИЕ —
            <ul>    
                <li>вы написали: "{{ $request.query }}"</li>
            </ul>
            Вы уверены, что хотите перейти к другому вопросу? (Да/Нет)
            <ul>
                <li>нажмите "[Да]" и работа по предыдущему вопросу прервётся, нажмите "[Нет]" и мы продожим рассмотрение предыдущего вопроса.</li>
            </ul>
        inlineButtons:
            {text:"Да"}  -> ./ContextChangeAcception-yes
            {text:"Нет"} -> ./ContextChangeAcception-no

        state: ContextChangeAcception-yes 
            intent: /Общие/Согласие
            go!: {{ $session.nextState }}
   
        state: ContextChangeAcception-no
            intent: /Общие/Отказ
            go!: {{ $session.lastState }}

# ------------------------- Переключение на оператора -------------------------

    #можно просто написать "ожидайте, пожалуйста..." 
    state: Operator || noContext=true
        intent!: /Общие/Operator
        script:
            sleep();
#        a:  — Подключаю оператора —
#            <ul>
#                <li>для возврата к виртуальному помощнику введите любую из команд: /close, /bot, /закрыть чат, /вернуть бота.</li>
#            </ul>
#            Ожидайте, пожалуйста...
        go!: ./Switch
        
        state: Switch || noContext=true
            script:
                $session.operatorTransCount = $session.operatorTransCount || 0; ++$session.operatorTransCount;      //# считаем количество попыток переключения на оператора (пользовательская переменная)
                $session.noMatchCount = $session.noMatchCount || 0;                                                 //# задаем счетчик по умолчанию, если undefined, чтобы не было ошибки в последующем if (пользовательская переменная)

                //#Если количество попыток переключения удовлетворяет нормативу, то бот разрешает переключить (то есть выставляет флаг $session.OperatorTransFlag == true
                if ($session.OperatorTransFlag == true
                || $session.operatorTransCount > 2
                || ($session.operatorTransCount == 2 && $session.noMatchCount == 0)
                || $session.noMatchCount > 1) {
                    //# запускаем функцию переключения на оператора
                    smartSwitch($session.smartSwitchLastState);
                } else {
                    //#Если это первая попытка переключения, то бот предлагает переформулировать вопрос
                    if ($session.operatorTransCount == 1 && $session.noMatchCount == 0) {
                        $reactions.answer('Моя задача - помочь вам без необходимости подключения оператора. <ul><li>операторы обычно заняты сложными вопросами и лучше их не отвлекать. Если не получиться помочь, то я попробую переключить вас на оператора.</li></ul>Переформулируйте, пожалуйста, ваш вопрос в краткой форме. <ul><li>либо введите фразу "[все навыки]" для ознакомления с вопросами, на которые я быстро отвечаю.</li></ul>');
                        $reactions.inlineButtons({text: "Все навыки"});
                    };
                    //#Если бот не знает, что ответить, то он предлагает переформулировать вопрос
                    if ($session.noMatchCount == 1 && $session.operatorTransCount == 1) {
                        $reactions.answer('На ваше сообщение я не нахожу ответа, пожалуйста, попробуйте переформулировать его в краткой форме. <ul><li>если я не найду подходящего ответа, тогда - переключу на оператора.</li></ul> Для ознакомления с вопросами, на которые я быстро отвечаю введите фразу "[все навыки]".');
                        $reactions.inlineButtons({text: "Все навыки"});
                    };
                    //#Если бот не знает, что ответить, и пользователь пытается сразу переключиться на оператора
                    if ($session.noMatchCount == 1 && $session.operatorTransCount == 2) {
                        $reactions.answer('Моя задача - помочь вам без необходимости подключения оператора. <ul><li>операторы обычно заняты сложными вопросами и лучше их не отвлекать. Если не получиться помочь, то я попробую переключить вас на оператора.</li></ul>Переформулируйте, пожалуйста, ваш вопрос в краткой форме. <ul><li>либо введите фразу "[все навыки]" для ознакомления с вопросами, на которые я быстро отвечаю.</li></ul>');
                        $reactions.inlineButtons({text: "Все навыки"});
                    };
                };

#        state: Switch || noContext=true
#            script:
#                $context.session.ltxFirstMessage = "—————————————————" + "\n" + "ИСТОРИЯ ДИАЛОГА до переключения на оператора:" + "\n" + $context.client.chatHistory.map(function(val) {return val.type + "\n" + val.state + "\n" + val.text;}).join("\n\n") + "\n" + "—————————————————" + "\n" + "Рекомендации бота (тематики):" + "\n" + strSortNCount($context.session.chatHistorySTAT);
#                $response.replies.push({
#                    type:"switch",
#                    appendCloseChatButton: true,
#                    closeChatPhrases: ["/close", "/сдщыу",
#                                        "/bot", "/ище","/бот", "/,jn",
#                                        "/закрыть чат", "/pfrhsnm xfn", "/закройте чат", "/pfrhjqnt xfn", "/закрой чат", "/pfrh* xfn",
#                                        "/вернуть бота", "/dthyenm ,jnf", "/верните бота", "/dthybnt ,jnf", "/верни бота", "/dthyb ,jnf",
#                                        "\close", "\сдщыу",
#                                        "\bot", "\ище","\бот", "\,jn",
#                                        "\закрыть чат", "\pfrhsnm xfn", "\закройте чат", "\pfrhjqnt xfn", "\закрой чат", "\pfrh* xfn",
#                                        "\вернуть бота", "\dthyenm ,jnf", "\верните бота", "\dthybnt ,jnf", "\верни бота", "\dthyb ,jnf",
#                                        "закрыть чат", "pfrhsnm xfn", "закройте чат", "pfrhjqnt xfn", "закрой чат", "pfrh* xfn",
#                                        "вернуть бота", "dthyenm ,jnf", "верните бота", "dthybnt ,jnf", "верни бота", "dthyb ,jnf"],
#                    firstMessage: $context.session.ltxFirstMessage,
#                    lastMessage: "— Диалог завершён —",
#                    attributes: {
#                        "Рекомендации бота (тематики)": strSortNCount($context.session.chatHistorySTAT),
#                    }
#                });
            
            #Действие, если оператор закрыл чат
            #или если чат закрыт по команде пользователя или нажатием закрывающей кнопки
            # $session.smartSwitchCheck проверяет, что intent!: /Общие/CloseLivechatPhrases сработал после общения с оператором или просто во время сценария так как при закрытии чата с оператором последняя фраза клиента почемуто прилетает боту и ему приходится на нее реагировать помимо event!: livechatFinished
            state: livechatFinished || noContext=true
                event!: livechatFinished
                intent!: /Общие/CloseLivechatPhrases
                script:
                    //# действия в случаях когда преход в этот стейт осуществлен по ивенту (event!: livechatFinished) или по интенту (intent!: /Общие/CloseLivechatPhrases)
                    //# $session.smartSwitchCheck == true для случая когда по ивенту.
                    if ($session.smartSwitchCheck == true) {
                        $session.smartSwitchCheck = null; 
                        $session.operatorChatOver = true; //#параметр передает значение, что чат с оператором закончен
                        $reactions.answer('— Оператор отключен — <ul><li>с вами виртуальный помощник Ииигорь.</li></ul>');
                        $reactions.transition({value: "/Оценка", deferred: false});                 //# deferred: false = go!:
                    } else {
                        $reactions.answer('— Вы общаетесь с виртуальным помощником Ииигорем —');
                        $reactions.transition({value: "/ВопросУточните", deferred: false});         //# deferred: false = go!:
                    };
                    
#                if: $session.smartSwitchCheck == true;
#                    script:
#                        $session.smartSwitchCheck = null; //#параметр проверяет преходи в этот стейт осуществлен после ивента завершения чата с  оператором или просто так по интенту
#                        $session.operatorChatOver = true; //#параметр передает значение, что чат с оператором закончен
#                    a:  — Оператор отключен —
#                        <ul>
#                            <li>с вами виртуальный помощник.</li>
#                        </ul>
#                    go!: /Оценка
#                else:
#                    a:  — Вы общаетесь с виртуальным помощником —
#                    go!: /ВопросУточните
            
            #Действие, если нет доступных операторов в линии
            state: NoOperatorsOnline || noContext=true
                event!: noLivechatOperatorsOnline
                script:
                    if ($session.smartSwitchlastGroup != "G20sup"
                    &&  $session.smartSwitchlastGroup != "all")
                        {
                            $reactions.answer('— Нет доступных операторов — <ul><li>пробую переключить на другую группу.</li></ul>');
                            smartSwitch();
                        }
                    else
                        {
                            $session.smartSwitchCheck = null;
                            $reactions.answer('— Нет доступных операторов —<ul><li>график работы операторов с 10 до 19 (МСК, пн-пт);</li><li>возврат к общению с виртуальным помощником Ииигорем.</li></ul><br>— С вами снова виртуальный помощник Ииигорь —<ul><li>напишите иной вопрос или попробуйте позже.</li></ul>');
                            
                        }
#                a:  — Нет доступных операторов —
#                    <ul>
#                        <li>график работы операторов с 10 до 19 (МСК) ежедневно;</li>
##                        <li>рекомендуем выбрать время обращения в <a href="https://hoff.ru/contacts/zagruzhennost-koll-tsentra/" target="_blank">период меньшей загруженности колл-центра</a>.</li>
#                    </ul>
#                a:  — С вами снова виртуальный помощник —
#                    <ul>
#                        <li>напишите другой вопрос или попробуйте позже.</li>
#                    </ul>

# ---------------- Реакции на системные события (ивенты)  ---------------------

    state:
        event!: sendFile
        a: — Ваш файл получен —

# ---------------------- Прощание перед закрытием чата ------------------------
    
    #Прощание с клиентом
    state: Bye
        intent!: /Общие/Bye
        script:
            sleep();
        random:
            a: До свидания! Появятся вопросы — обращайтесь.
            a: Всего хорошего! Будут вопросы — обращайтесь.
            a: Я прощаюсь с вами. Будут вопросы — обращайтесь.
        go!: ../Оценка
    
    #Оценка удовлетворенности клиента
    state: Оценка
        intent!: /Общие/CloseSession
        script:
            sleep();
            $session.operatorChatOver = null; //#обнуляет параметр после окончания общения с оператором
        a:  Всё ли вам понравилось?
            <ul>
                <li>оцените, пожалуйста, наш диалог от 1 до 5 (5 - отлично). Оставить комментарий можно будет на следующем шаге.</li>
#                <li>оцените, пожалуйста, наш диалог от 1 до 5 (где 5 — отлично), либо нажмите на одну из кнопок ниже. Оставить комментарий можно будет на следующем шаге.</li>
            </ul>
        buttons:   
            {text:"5 ★★★★★"}
            {text:"4 ★★★★"}
#            {text:"3 ★★★"}
            {text:"2 ★★"}
            {text:"1 ★"}
#        inlineButtons:   
#            {text:"5 Отлично 😁"}
#            {text:"4 Хорошо 😉"}
#            {text:"3 Сносно 😐"}
#            {text:"2 Плохо 😤"}
#            {text:"1 Ужасно 😡"}

    
        # Создаем две переменных так как клиент может не ответить на этот стейт и тогда вторая переменная не будет создана
        state: ОценкаКомментарий
            intent: /Общие/Bye
            intent: /Общие/Согласие
            intent: /Общие/Отрицание
            q: *
            script:
                $session.sci = $request.query;
            a:  Ваша оценка принята.
                <ul>
                   <li>при желании, напишите свой комменнтарий к оценке одним сообщением, и мы его обязательно рассмотрим, либо просто закройте чат.</li>
                </ul>

            state: ОценкаЗакрытие
                intent: /Общие/Bye
                intent: /Общие/Согласие
                intent: /Общие/Отрицание
                q: *
                script:
                    $session.sciComment = $request.query;
                a:  Спасибо, что помогаете нам стать лучше! Рады видеть вас снова.
                go!: /CloseSession

# ---------------- Выявление и уточнение вопросов клиента   -------------------

    #Действие, если клиент присылает приветственную фразу, тогда бот пытается сразу вывести его на конкретику.
    #Действие, если пользователь написал, что у него остались еще вопросы, но не обозначил какие именно.
    state: ВопросУточните || noContext=true
        intent!: /Общие/Hello
        intent!: /Общие/УМеняЕщеВопросы
        script:
            sleep();
        random:
            a:  Чем могу помочь?
            a:  Какой ваш вопрос?
            a:  Что бы вы хотели обсудить?
            a:  В чём ваш вопрос?

    #Действие, если требуется переформулировать вопрос клиента (чаще всего, когда он задается в неявной форме, без важных деталей).
    state: ВопросПереформулируйте || noContext=true
        intent!: /Общие/ВопросПереформулируйте
        script:
            sleep();
        random:
            a:  Уточните, пожалуйста, вопрос.
            a:  Прошу вас уточнить вопрос.
            a:  Опишите подробнее ваш вопрос.

    #Действие, если перед закрытием чата необходимо узнать остались ли у клиента еще вопросы.
    state: ВопросыОстались || noContext=true
        script:
            sleep();
        random:
            a:  Остались ли у вас вопросы?
                <ul>
                    <li>вы можете продолжить диалог, ответив на предыдущее сообщение, или обратиться с другим вопросом;</li>
                    <li>нажмите "[Завершить диалог]", если вопросов не осталось, либо вы можете поделиться своим мнением о работе нашего сервиса, нажав "[Поделиться впечатлением]".</li>
                </ul>
            a:  У вас остались вопросы?
                <ul>
                    <li>вы можете продолжить диалог, ответив на предыдущее сообщение, или обратиться с другим вопросом;</li>
                    <li>нажмите "[Завершить диалог]", если вопросов не осталось, либо вы можете поделиться своим мнением о работе нашего сервиса, нажав "[Поделиться впечатлением]".</li>
                </ul>
        inlineButtons:
            {text:"Завершить диалог"} -> /Bye
            {text:"Поделиться впечатлением"} -> /Оценка

    #Действие, если клиент поблагодарил бота.
    state: ThankYou || noContext=true
        intent!: /Общие/ThankYou
        script:
            sleep();
        random:
            a: Не за что.
            a: Всегда пожалуйста.
            a: Что уж там), обращайтесь.
        inlineButtons:
            {text:"Завершить диалог"} -> /Bye
            {text:"Поделиться впечатлением"} -> /Оценка

# ---------- Дейстивие, если клиент не отвечает (глобальный таймаут) ----------

    #Закрытие сессии по глобальному таймауту при неответе клиента
    state: CloseSessionByTimeout
        a: — Время диалога истекло —
        go!: /Bye

# ----------------------------- Закрытие сессии -------------------------------
    
    #Закрытие сессии
    state: CloseSession
        a:  — Диалог завершён —
            <ul>
                <li>для открытия нового диалога напишите свой вопрос в чат или нажмите "[вернуться в главное меню]".</li>
            </ul>
        inlineButtons:
            {text:"Вернуться в главное меню"}               -> /Начало/ЧтоТыУмеешь
            {text:"Оставить отзыв / сообщить об ошибке"}    -> /Оценка
        script:
            //#Создание переменной с путем на историю переписки по данной сессии:
            $session.sciLink = "https://zenbot-dev.just-ai.com/hoff_bot_prj02_maste-118482474-dvj/statistic/dialogs/";
            //#Отправка отзыва клиента в гугл таблицу через вебхук на стороне сервиса IFTTT:
            $session.wsUrl = 'https://maker.ifttt.com/trigger/HoffBot-PRJ02-mainsc-CSI-Log/with/key/iawiiWUbHFs2L2Yf8oGU538Z62W_1kyS3xy252qWHAX';
            $session.wsMethod = "POST";
            $session.wsBody = { "value1" : "",
                                "value2" : $session.sci +" | "+ $session.sciComment +" | "+ $session.sciLink,
                                "value3" : $session.sciLink };
            $session.wsTransGood = {value: "/CloseSession", deferred: true};
            $session.wsTransBad  = {value: "/CloseSession", deferred: true};
            var $httpResponse = getStatus();
                //$reactions.transition($session.wsTransGood); //#в более примитвном сценарии эта строка потребуется
            //#Обнуление счётчиков и флагов не смотря на то что завершение сессии их автоматически очистит
            $session.OperatorTransFlag = false;
#            //$session.operatorTransCount = 0;
#            //$session.noMatchCount = 0;
            //#Закрытие сессии
            $jsapi.stopSession();

# ----------------------------- Стейт для тестов  -----------------------------

    state: infotest
        q!: (инфотест | byajntcn | infotest | штащеуые)
        script: 
            $reactions.answer("1");
            $reactions.answer("2");
            $reactions.answer("3");
            $reactions.answer("4");

#        a:  $session.start:
#            {{ JSON.stringify($session.start) }}
#            
#            $client:
#            {{ JSON.stringify($client) }}

    #история переписки
#    state: история
#        q!: история
#        script:
#            //$session.histStat = "/003/ЛКБ-01-01" + "/003/ЛКБ-02-02/003/ЛКБ-01-01";
#            //$session.histStat2 = strSortNCount($context.session.chatHistorySTAT);
#            $session.hist = $context.client.chatHistory.map(function(val) {return val.type + "\n" + val.state + "\n" + val.text;}).join("\n\n") + "\n" + "—————————————————" + "\n" + "Тематики обращения:" + "\n" + strSortNCount($context.session.chatHistorySTAT);
#        a: {{$session.hist}}
#
#    state: тест
#        q!: тест
#        a:  тест Operator-G01-zrp
#        go!: /Operator-G01-zrp

#    #Нужен, когда используется переопределение стейта чтобы в тестах бот не повторял ответы при проверке переходов в один и тот же стейт по синонимичным фразам клиентов
#    state: антиповтор || noContext=true
#        q!: антиповтор
#        a: ....

# ------------------- Ответы на общие частые вопросы  -------------------------

    #Уточнение положений политики конфиденциальности
    state: Конфиденциальность || noContext=true
        intent!: /Общие/Конфиденциальность
        script:
            sleep();
        a: Политика конфиденциальности Hoff была разработана с целью защиты персональных данных клиентов Hoff и соблюдения законодательства Российской Федерации. С ней вы можете ознакомиться по ссылке <a href="https://hoff.ru/services/privacy_policy/" target="_blank">https://hoff.ru/services/privacy_policy/</a>

# -------------------------- Отправка почтой  ---------------------------------

    #### Сценарий отправки обращения по email в разработке ###
    
    #Уточнение положений политики конфиденциальности
    state: EmailSend
        intent!: /Общие/EmailSend
        script:
            sleep();
            //$session.EmailSendLastState = "";
            if ((($client || {}).user || {}).email
            &&  (($client || {}).user || {}).tabNomer
            &&  $client.user.email.toLowerCase().indexOf('hoff.ru') > -1)
                {
                    $reactions.answer('Для оформления вашего обращения по почте подтвердите данные. Ваш табельный номер: {{$client.user.tabNomer}}, а корпоративный email: {{$client.user.email}}. Всё верно? (да/нет)');
                    $session.EmailFrom = $client.user.email;
                    // $session.EmailTo
                    $reactions.inlineButtons({text: "Да, всё верно", transition: "./EmailSendConfirmGood"});
                    $reactions.inlineButtons({text: "Нет (ошибка в данных / они не мои)", transition: "./EmailSendConfirmBad"});
                } 
            else
                {
                    $reactions.answer('К сожалению, мне не удалось определелить адрес вашей корпоративной электронной почты. Пожалуйста, проверьте, что в вашем профиле на корпоративном портале time.Hoff.ru ваши данные (ФИО, табельный номер и корпоративный Email) указаны верно или обратитесь повторно в часы работы операторов.');
                };

        state: EmailSendConfirmBad
            intent: /Общие/Отказ
            script:
                sleep();
            a:  При ошибках в указании вашего табельного номера и/или основного корпоративного адреса электронной почты проверьте, пожалуйста, что вход в портал осуществлен под вашей учётной записью, и правильно ли занесены в вашу карточку на корпоративном поратле Hoff ваши учётные данные.
                - если осуществлён вход под чужими учётными данными, то выйдите и зайдите под своими;
                - если данные в вашей карточке на корпоративном портале Hoff содержат ошибку, то позвоните в [helpdesk] на внутренний номер 333+1 или напишите письмо на почту <a href="mailto:helpdesk@hoff.ru" target="_blank">helpdesk@hoff.ru</a>.

        state: EmailSendConfirmGood
            intent: /Общие/Согласие
            script:
                sleep();
            a:  Укажите, пожалуйста, коротко тему вашего письма (в одно сообщение).
        
            state: EmailConfirmed
                q: *
                script:
                    sleep();
                    $session.EmailSubject = $request.query;
                a:  Укажите коротко суть вашего обращения (в одно сообщение):
                    - то есть коротко опишите ситуацию, что произошло и/или что вам необходимо.
                    
                    Для справки:
                    - помимо этого к письму будет прикреплена история нашей переписки;
                    - и при необходимости, в ответном письме, оператор запросит уточняющую информацию.
    
                state: Subject
                    q: *
                    script:
                        sleep();
                        $session.EmailSubject = $request.query;
                    a:  Спасибо.
                        Теперь также в одно сообщение укажите коротко суть вашего обращения.
                        - помимо этого к письму будет прикреплена история нашей переписки;
                        - при необходимости, в ответном письме, специалист, назначенный на решение вашего вопроса, запросит уточняющую информацию.
                
                    state: Body
                        q: *
                        script:
                            sleep();
                            $session.EmailBody = $request.query;
                        a:  Теперь подтвердите, пожалуйста, что переданные вами данные верны.
                            Письмо с обращением будет отправлено...
                            
                            От имени сотрудника:
                            - {{}}
                            
                            С электронной почты: 
                            - {{$session.EmailFrom}} 
                            
                            Тема обращения:
                            - {{$session.EmailSubject}}
                            
                            Суть обращения: 
                            - {{$session.EmailBody}}
                            
                            К письму также будет прикреплена история переписки по данному диалогу.
                            
                            Всё верно?
                            - нажмите "[Да]", чтобы отправить письмо;
                            - при обнаруженеии ошибки в данных нажмите "[Нет]" и повторите их ввод.
                        inlineButtons:
                            {text:"Да, отправить письмо"} -> ./Result
                            {text:"Нет, повторить ввод данных"} -> ./BadData
                        
                        state: BadData
                            intent: /Общие/Отказ
                            script:
                                sleep();
                            go!: /EmailSend/EmailSendConfirmGood/

                        state: Result
                            intent: /Общие/Согласие
                            script:
                                sleep();
                            a:  Ваше обращение отправлено по электронной почте.
                                В течение нескольких минут вам на почту {{$session.EmailFrom}} поступит письмо от Helpdesk, в котором будет указана информация:
                                - о принятии вашего письма в обработку;
                                - номер, присвоенный вашему обращению;
                                Дальнейшую переписку по обращению вы сможете вести ответив на данное письмо. Вся информация по вашему обращению будет доступна на портале Helpdesk по адресу:
                                - http://helpdesk.kifr-ru.local:81/
                                При отсутствии ответного письма обращайтесь в Helpdesk по внутреннему телефону: 333+1.