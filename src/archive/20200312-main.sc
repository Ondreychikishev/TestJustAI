#В этом файле используется разметка Битрикс в ответах бота.
#В этом файле используется разметка Битрикс в ответах бота.
#В этом файле используется разметка Битрикс в ответах бота.
#В этом файле используется разметка Битрикс в ответах бота.
#В этом файле используется разметка Битрикс в ответах бота.

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

require: scenarios/001-support-FAQ-hr.sc

#require: scenarios/edu02.sc

# -----------------------------------------------------------------------------
# --------------------------- Скрипты-обработчики -----------------------------
# -----------------------------------------------------------------------------

init:

# Запрос переопределение всех ошибок

    bind("onAnyError", function($context) {
        $reactions.answer('К сожалению, по техническим причинам я не смогу сейчас помочь. Задайте другой вопрос или введите "[Переключить на оператора]"');
        $reactions.inlineButtons({text: "Переключить на оператора", transition: "/Operator"})
    });

# Глобальные таймауты при неответе клиента (120 = 2 мин | 180 = 3 мин | 300 сек = 5 мин)
# метод .startsWith(); проверяет текст, начиная с начала строки, поэтому все вложенные стейты тоже буду ему сответствовать.  

    #Только, когда находимся в любом стейте, кроме уточнения вопросов, прощания, оценки или закрытия сессии
    bind("postProcess", function() {
        if (!$jsapi.context().currentState.startsWith("/ВопросыОстались")                               // кроме стейта, уточняющего наличие доп вопросов у клиента
        &&  !$jsapi.context().currentState.startsWith("/ВопросПереформулируйте")                        // кроме стейта, с просьбой переформулировать вопрос
        &&  !$jsapi.context().currentState.startsWith("/ВопросУточните")                                // кроме стейта, с просьбой уточнить вопрос
        &&  !$jsapi.context().currentState.startsWith("/Operator")                                      // кроме стейта, переводящего на стейт Switch
        &&  !$jsapi.context().currentState.startsWith("/CloseSessionByTimeout")                         // кроме стейта, переводящего на стейт Bye по таймауту
        &&  !$jsapi.context().currentState.startsWith("/Bye")                                           // кроме стейта, переводящего на оценку удовлетворенности
        &&  !$jsapi.context().currentState.startsWith("/Оценка")                                        // кроме стейта, уточняющего уровень удовлетовренности у клиента (также на этот стейт попадаем, когда оператор закрывает чат)
        &&  !$jsapi.context().currentState.startsWith("/CloseSession"))                                 // кроме стейта, закрывающего сессию
            {
                $reactions.timeout({interval: 120, targetState: '/ВопросыОстались'});
            }        
    });

    #Только, когда находимся в стейте, уточняющем не сотались ли еще вопросы
    bind("postProcess", function() {
        if ($jsapi.context().currentState.startsWith("/ВопросыОстались")                                // когда в стейте, уточняющем наличие доп вопросов у клиента
        ||  $jsapi.context().currentState.startsWith("/ВопросПереформулируйте")                         // когда в стейте, с просьбой переформулировать вопрос
        ||  $jsapi.context().currentState.startsWith("/ВопросУточните"))                                // когда в стейте, с просьбой уточнить вопрос
            {
                $reactions.timeout({interval: 180, targetState: '/CloseSessionByTimeout'});
            }        
    });

    #Только, когда находимся в стейтах прощания или оценки
    bind("postProcess", function() {
        if ($jsapi.context().currentState.startsWith("/Bye")                                            // когда в стейте, переводящем на оценку удовлетворенности
        ||  $jsapi.context().currentState.startsWith("/Оценка"))                                        // когда в стейте, уточняющем уровень удовлетовренности у клиента (также на этот стейт попадаем, когда оператор закрывает чат)
            {
                $reactions.timeout({interval: 300, targetState: '/CloseSession'});
            }        
    });

# Переопределение ответа бота, когда он начинает повторяться

    bind("postProcess", function($context) {
        var currentAnswer = $context.response.replies.reduce(function(allAnswers, reply) {
            allAnswers += reply.type === "text" ? reply.text : "";  
            return allAnswers;
        },"");
        if ($context.session.lastAnswer === currentAnswer) {
            $context.response.replies = [
            {
                "type":"text",
                "text":"..."
            }];
            $reactions.transition("/Operator");
        }
        $context.session.lastAnswer = currentAnswer;
    });

# Переопределение последовательнсоти матчера

    bind("selectNLUResult", function(ctx) {                                     // задаем обработчик для фазы `selectNLUResult`
            log(ctx.nluResults);                                                // выводим результаты в лог
            if (ctx.nluResults.intents.length > 0)
                {
                    ctx.nluResults.selected = ctx.nluResults.intents[0];        // используем результат от интентов
                    return;
                }
            if (ctx.nluResults.patterns.length > 0)
                {
                    ctx.nluResults.selected = ctx.nluResults.patterns[0];       // если результата от интентов нет, используем паттерны
                    return;
                }
            if (ctx.nluResults.examples.length > 0)
                {
                    ctx.nluResults.selected = ctx.nluResults.examples[0];       // если результата от интентов и паттернов нет, используем примеры
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
# Запрос препроцесс собирает реплики клиента, а постпроцесс - реплики бота
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

# -----------------------------------------------------------------------------
# ----------------------------- Главный сценарий ------------------------------
# -----------------------------------------------------------------------------

theme: /

    state: Начало
        intent!: /Общие/MainMenu
        intent!: /Общие/Hello
        q!: (*start/*старт/*ыефке/*cnfhn)
        a:  Привет! Я виртуальный помощник сотрудников Hoff. Могу подсказать:
                - даты выплат [send=зарплаты]зарплаты[/send] и [send=премии]премии[/send];
                - [send=коды магазинов]коды магазинов[/send], дирекций, прочих БЮ и кто их руководитель;
                - [send=расписание автобуса]расписание автобуса[/send];
                - [URL=http://confluence.kifr-ru.local:8090/pages/viewpage.action?pageId=46041071]Все навыки[/URL].
            По другим кадровым вопросам:
                - я подключаю специалистов по графику (c 10 до 18 МСК, пн-пт).
            По техническим вопросам:
                - звоните в [send=helpdesk]helpdesk[/send] на внутренний номер 333+1.
            Спрашивайте, не стесняйтесь... ;)
        random:
            a:  Чем могу помочь?
            a:  Какой Ваш вопрос?
            a:  Что бы Вы хотели обсудить?
            a:  В чём Ваш вопрос?

        state: ЧтоТыУмеешь || noContext=true
            intent!: /Общие/ЧтоТыУмеешь
            q: все навыки
            script:
                sleep();
            a:  Со всеми вопросами, на которые я отвечаю, можно ознакомиться по ссылке [URL=http://confluence.kifr-ru.local:8090/pages/viewpage.action?pageId=46041071]Посмотреть[/URL].
                Спрашивайте, не стесняйтесь...

        state: helpdesk || noContext=true
            intent!: /Общие/Helpdesk
            q: (*техвопрос | *тех вопрос | *helpdesk | *рудзвуыл)
            q: (*helpdesk | *рудзвуыл | *хелпдеск | *tkgltcr)
            script:
                sleep();
            a:  В службе технической поддержки Hoff:
                    - работают квалифицированные специалисты, только они обладают сверхспособностью договариваться с техникой, чтобы она снова заработала, даже казалось бы в безнадёжных ситуациях.
                При проблемах с техникой:
                    - смело звоните в helpdesk на внутренний номер 333+1;
                    - или пишите письмо на почту [URL=mailto:helpdesk@hoff.ru]helpdesk@hoff.ru[/URL]
                В обоих случаях:
                    - будьте готовы указать Ваш [B]табельный номер[/B], а также имя компьютера (на котором возникла проблема) или его IP адрес;
                    - конечно же, наизусть достаточно помнить только табельный номер, остальные данные чаще всего выведены на рабочий стол компьютера справа над часами и датой (Host Name - это имя компьютера, IP Addres - это IP адрес компьютера), либо приклены стикером на монитор.
            a:  И ещё, последнее по порядку, но не по значению:
                    - обращаясь в тех. поддержку всегда будьте вежливы и корректны, помните, что специалист, принимающий Вашу заявку и специалист, занимающийся ее решением могут оказаться разными людьми, да ещё и работающими в разных отделах, а то и в разных городах или даже частях нашей страны.
                    - работа специалиста в линии связана с определенной долей стресса, когда за короткий промежуток времени необходимо принять ответственное решение или правильно оформить Вашу заявку и отправить её по назначению, поэтому хорошим тоном с Вашей стороны будет как можно точнее описать суть проблемы, нежели начинать диалог с высказывания недовольства по поводу сложившейся ситуации, даже при повторном обращении.
                    - подавляющая часть процессов в работе тех. поддержки уже отлажена и проверена на значимом количестве ситуаций с учётом всех деталей, поэтому бессмысленно настаивать на своём видении того, как должно быть, Вы лишь в праве предложить свой вариант при желании, но не преподносить его как единственно верный.
        
# ------------------- Действие, если бот непонимает клиента -------------------
    
    #отлов всех нераспознанных фраз клиента
    state: CatchAll || noContext=true
        event!: noMatch
        script:
            sleep();
        go!: /Operator

# ----------------------- Уведомление о смене контекста -----------------------

    state: ContextChangeAcception
        script:
            $response.replies = [];
        a:  — ВНИМАНИЕ —
                - Вы написали: "{{ $request.query }}"
            Вы уверены, что хотите перейти к другому вопросу? (Да/Нет)
                - если "Да", то работа по предыдущему вопросу прервётся.
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
        a:  — Подключаю оператора —
                - для возврата к виртуальному помощнику введите любую из команд: /close, /bot, /закрыть чат, /вернуть бота.
            Ожидайте, пожалуйста...
        go!: ./Switch

        state: Switch || noContext=true
            script:
                $context.session.ltxFirstMessage = "—————————————————" + "\n" + "ИСТОРИЯ ДИАЛОГА до переключения на оператора:" + "\n" + $context.client.chatHistory.map(function(val) {return val.type + "\n" + val.state + "\n" + val.text;}).join("\n\n") + "\n" + "—————————————————" + "\n" + "Рекомендации бота (тематики):" + "\n" + strSortNCount($context.session.chatHistorySTAT);
                $response.replies.push({
                    type:"switch",
                    appendCloseChatButton: false,
                    closeChatPhrases: ["/close", "/сдщыу",
                                        "/bot", "/ище","/бот", "/,jn",
                                        "/закрыть чат", "/pfrhsnm xfn", "/закройте чат", "/pfrhjqnt xfn", "/закрой чат", "/pfrh* xfn",
                                        "/вернуть бота", "/dthyenm ,jnf", "/верните бота", "/dthybnt ,jnf", "/верни бота", "/dthyb ,jnf",
                                        "\close", "\сдщыу",
                                        "\bot", "\ище","\бот", "\,jn",
                                        "\закрыть чат", "\pfrhsnm xfn", "\закройте чат", "\pfrhjqnt xfn", "\закрой чат", "\pfrh* xfn",
                                        "\вернуть бота", "\dthyenm ,jnf", "\верните бота", "\dthybnt ,jnf", "\верни бота", "\dthyb ,jnf",
                                        "закрыть чат", "pfrhsnm xfn", "закройте чат", "pfrhjqnt xfn", "закрой чат", "pfrh* xfn",
                                        "вернуть бота", "dthyenm ,jnf", "верните бота", "dthybnt ,jnf", "верни бота", "dthyb ,jnf"],
                    firstMessage: $context.session.ltxFirstMessage,
                    lastMessage: "Мы ждём Вас снова!",
                    attributes: {
                        "Рекомендации бота (тематики)": strSortNCount($context.session.chatHistorySTAT),
                    }
                });
            
            #Действие, если оператор закрыл чат или клиент послал закрывающую фразу
            state: livechatFinished || noContext=true
                event!: livechatFinished
                q: (*close | *сдщыу)
                q: (*bot | *ище | *бот | *,jn)
                q: (*закрыть чат | *pfrhsnm xfn | *закройте чат | *pfrhjqnt xfn | *закрой чат | *pfrh* xfn)
                q: (*вернуть бота | *dthyenm ,jnf | *верните бота | *dthybnt ,jnf | *верни бота | *dthyb ,jnf)
                a:  — Оператор отключен —
                        - с Вами снова виртуальный помощник.
                go!: /Оценка
            
            #Действие, если нет доступных операторов в линии
            state: NoOperatorsOnline || noContext=true
                event!: noLivechatOperatorsOnline
                a:  — Нет доступных операторов —
                        - график работы операторов с 10 до 18 (МСК) ежедневно;
                        - рекомендуем Вам выбрать время обращения в период меньшей загруженности колл-центра [URL=https://hoff.ru/contacts/zagruzhennost-koll-tsentra/]подробнее[/URL].
                    С Вами снова виртуальный помощник:
                        - напишите другой вопрос или попробуйте позже.

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
            a: До свидания! Появятся вопросы - обращайтесь.
            a: Всего хорошего! Будут вопросы - обращайтесь.
            a: Я прощаюсь с Вами. Будут вопросы - обращайтесь.
        go!: ../Оценка
    
    #Оценка удовлетворенности клиента
    state: Оценка
        intent!: /Общие/CloseSession
        script:
            sleep();
        a:  Всё ли Вам понравилось?
                - оцените, пожалуйста, наш диалог от 1 до 5 (где 5 - Отлично), либо нажмите на одну из кнопок ниже.
        inlineButtons:   
            {text:"5 ★★★★★"}
            {text:"4 ★★★★"}
            {text:"3 ★★★"}
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
                    - при желании, напишите свой комменнтарий к оценке одним сообщением, и мы его обязательно рассмотрим, либо просто закройте чат.

            state: ОценкаЗакрытие
                intent: /Общие/Bye
                intent: /Общие/Согласие
                intent: /Общие/Отрицание
                q: *
                script:
                    $session.sciComment = $request.query;
                a:  Спасибо, что помогаете нам стать лучше! Рады видеть Вас снова.
                go!: /CloseSession

# ---------------- Выявление и уточнение вопросов клиента   -------------------

    #Действие, если клиент присылает приветственную фразу, тогда бот пытается сразу вывести его на конкретику.
    #Действие, если пользователь написал, что у него остались еще вопросы, но не обозначил какие именно.
    state: ВопросУточните || noContext=true
#        intent!: /Общие/Hello
        intent!: /Общие/УМеняЕщеВопросы
        script:
            sleep();
        random:
            a:  Чем могу помочь?
            a:  Какой Ваш вопрос?
            a:  Что бы Вы хотели обсудить?
            a:  В чём Ваш вопрос?

    #Действие, если требуется переформулировать вопрос клиента (чаще всего, когда он задается в неявной форме, без важных деталей).
    state: ВопросПереформулируйте || noContext=true
        intent!: /Общие/УточнитеВопрос
        script:
            sleep();
        random:
            a:  Уточните, пожалуйста, вопрос.
            a:  Прошу Вас уточнить вопрос.
            a:  Опишите подробнее Ваш вопрос.

    #Действие, если перед закрытием чата необходимо узнать остались ли у клиента еще вопросы.
    state: ВопросыОстались || noContext=true
        script:
            sleep();
        random:
            a:  Остались ли у Вас вопросы?
                    - Вы можете продолжить диалог, ответив на предыдущее сообщение, или обратиться с другим вопросом;
                    - нажмите "Завершить диалог", если вопросов не осталось.
            a:  У Вас остались вопросы?
                    - Вы можете продолжить диалог, ответив на предыдущее сообщение, или обратиться с другим вопросом;
                    - нажмите "Завершить диалог", если вопросов не осталось.
        inlineButtons:
            {text:"Завершить диалог"} -> /Bye


# ---------- Дейстивие, если клиент не отвечает (глобальный таймаут) ----------

    #Закрытие сессии по глобальному таймауту при неответе клиента
    state: CloseSessionByTimeout
        a: — Время диалога истекло —
        go!: /Bye

# ----------------------------- Закрытие сессии -------------------------------
    
    #Закрытие сессии
    state: CloseSession
        a:  — Диалог завершён —
                - для открытия нового диалога напишите свой вопрос в чат.
        script:
            // Создание переменной с путем на историю переписки по данной сессии:
            $session.sciLink = "https://zenbot-dev.just-ai.com/hoff_bot_prj02_maste-118482474-dvj/statistic/dialogs/";
            // Отправка отзыва клиента в гугл таблицу через вебхук на стороне сервиса IFTTT:
            $session.wsUrl = 'https://maker.ifttt.com/trigger/HoffBot-PRJ02-mainsc-CSI-Log/with/key/iawiiWUbHFs2L2Yf8oGU538Z62W_1kyS3xy252qWHAX';
            $session.wsMethod = "POST";
            $session.wsBody = { "value1" : "",
                                "value2" : $session.sci +" | "+ $session.sciComment +" | "+ $session.sciLink,
                                "value3" : $session.sciLink };
            $session.wsTransGood = {value: "/CloseSession", deferred: true};
            $session.wsTransBad  = {value: "/CloseSession", deferred: true};
            var $httpResponse = getStatus();
                $reactions.transition($session.wsTransGood);
            $jsapi.stopSession();

# ----------------------------- Стейт для тестов  -----------------------------

    #Нужен, когда используется переопределение стейта чтобы в тестах бот не повторял ответы при проверке переходов в один и тот же стейт по синонимичным фразам клиентов
    state: антиповтор || noContext=true
        q!: антиповтор
        a: ....

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
#        a:  тест

# ------------------- Ответы на общие частые вопросы  -------------------------

    #Уточнение положений политики конфиденциальности
    state: Конфиденциальность || noContext=true
        intent!: /Общие/Конфиденциальность
        script:
            sleep();
        a: Политика конфиденциальности Hoff была разработана с целью защиты персональных данных клиентов Hoff и соблюдения законодательства Российской Федерации. С ней Вы можете ознакомиться по ссылке <a href="https://hoff.ru/services/privacy_policy/" target="_blank">https://hoff.ru/services/privacy_policy/</a>
