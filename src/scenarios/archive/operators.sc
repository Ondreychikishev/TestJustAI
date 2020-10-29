# ЭТОТ СЦЕНАРИЙ НЕ ИСПОЛЬЗУЕТСЯ

# Сценарий для выстраивания логики подключения различных групп операторов
# Описание групп операторов http://confluence.kifr-ru.local:8090/pages/viewpage.action?pageId=46041060

theme: /

# ------------------------- Переключение на оператора -------------------------

    #Супероператоры
    state: Operator-G20-sup || noContext=true
        script:
            sleep();
        a:  — Подключаю оператора —
            <ul>
                <li>для возврата к виртуальному помощнику введите любую из команд: /close, /bot, /закрыть чат, /вернуть бота.</li>
            </ul>
            Ожидайте, пожалуйста...
        go!: ./Switch

        state: Switch || noContext=true
            script:
                $context.session.ltxFirstMessage = "—————————————————" + "\n" + "ИСТОРИЯ ДИАЛОГА до переключения на оператора:" + "\n" + $context.client.chatHistory.map(function(val) {return val.type + "\n" + val.state + "\n" + val.text;}).join("\n\n") + "\n" + "—————————————————" + "\n" + "Рекомендации бота (тематики):" + "\n" + strSortNCount($context.session.chatHistorySTAT);
                $response.replies.push({
                    type:"switch",
                    destination: "G20-sup",
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
                    lastMessage: "— Диалог завершён —",
                    attributes: {
                        "Рекомендации бота (тематики)": strSortNCount($context.session.chatHistorySTAT),
                    }
                });

# ------------------------- Переключение на оператора -------------------------

    #Зарплата
    state: Operator-G01-zrp || noContext=true
        script:
            sleep();
        a:  — Подключаю оператора —
            <ul>
                <li>для возврата к виртуальному помощнику введите любую из команд: /close, /bot, /закрыть чат, /вернуть бота.</li>
            </ul>
            Ожидайте, пожалуйста...
        go!: ./Switch

        state: Switch || noContext=true
            script:
                $context.session.ltxFirstMessage = "—————————————————" + "\n" + "ИСТОРИЯ ДИАЛОГА до переключения на оператора:" + "\n" + $context.client.chatHistory.map(function(val) {return val.type + "\n" + val.state + "\n" + val.text;}).join("\n\n") + "\n" + "—————————————————" + "\n" + "Рекомендации бота (тематики):" + "\n" + strSortNCount($context.session.chatHistorySTAT);
                $response.replies.push({
                    type:"switch",
                    destination: "G01-zrp",
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
                    lastMessage: "— Диалог завершён —",
                    attributes: {
                        "Рекомендации бота (тематики)": strSortNCount($context.session.chatHistorySTAT),
                    }
                });

# ------------------------- Переключение на оператора -------------------------

    #Кадровое администрирование
    state: Operator-G02-kad || noContext=true
        script:
            sleep();
        a:  — Подключаю оператора —
            <ul>
                <li>для возврата к виртуальному помощнику введите любую из команд: /close, /bot, /закрыть чат, /вернуть бота.</li>
            </ul>
            Ожидайте, пожалуйста...
        go!: ./Switch

        state: Switch || noContext=true
            script:
                $context.session.ltxFirstMessage = "—————————————————" + "\n" + "ИСТОРИЯ ДИАЛОГА до переключения на оператора:" + "\n" + $context.client.chatHistory.map(function(val) {return val.type + "\n" + val.state + "\n" + val.text;}).join("\n\n") + "\n" + "—————————————————" + "\n" + "Рекомендации бота (тематики):" + "\n" + strSortNCount($context.session.chatHistorySTAT);
                $response.replies.push({
                    type:"switch",
                    destination: "G02-kad",
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
                    lastMessage: "— Диалог завершён —",
                    attributes: {
                        "Рекомендации бота (тематики)": strSortNCount($context.session.chatHistorySTAT),
                    }
                });

# ------------------------- Переключение на оператора -------------------------

    #Обучение и наставничество
    state: Operator-G03-edu || noContext=true
        script:
            sleep();
        a:  — Подключаю оператора —
            <ul>
                <li>для возврата к виртуальному помощнику введите любую из команд: /close, /bot, /закрыть чат, /вернуть бота.</li>
            </ul>
            Ожидайте, пожалуйста...
        go!: ./Switch

        state: Switch || noContext=true
            script:
                $context.session.ltxFirstMessage = "—————————————————" + "\n" + "ИСТОРИЯ ДИАЛОГА до переключения на оператора:" + "\n" + $context.client.chatHistory.map(function(val) {return val.type + "\n" + val.state + "\n" + val.text;}).join("\n\n") + "\n" + "—————————————————" + "\n" + "Рекомендации бота (тематики):" + "\n" + strSortNCount($context.session.chatHistorySTAT);
                $response.replies.push({
                    type:"switch",
                    destination: "G03-edu",
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
                    lastMessage: "— Диалог завершён —",
                    attributes: {
                        "Рекомендации бота (тематики)": strSortNCount($context.session.chatHistorySTAT),
                    }
                });

# ------------------------- Переключение на оператора -------------------------

    #Отдел мотивации
    state: Operator-G04-mot || noContext=true
        script:
            sleep();
        a:  — Подключаю оператора —
            <ul>
                <li>для возврата к виртуальному помощнику введите любую из команд: /close, /bot, /закрыть чат, /вернуть бота.</li>
            </ul>
            Ожидайте, пожалуйста...
        go!: ./Switch

        state: Switch || noContext=true
            script:
                $context.session.ltxFirstMessage = "—————————————————" + "\n" + "ИСТОРИЯ ДИАЛОГА до переключения на оператора:" + "\n" + $context.client.chatHistory.map(function(val) {return val.type + "\n" + val.state + "\n" + val.text;}).join("\n\n") + "\n" + "—————————————————" + "\n" + "Рекомендации бота (тематики):" + "\n" + strSortNCount($context.session.chatHistorySTAT);
                $response.replies.push({
                    type:"switch",
                    destination: "G04-mot",
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
                    lastMessage: "— Диалог завершён —",
                    attributes: {
                        "Рекомендации бота (тематики)": strSortNCount($context.session.chatHistorySTAT),
                    }
                });

# ------------------------- Переключение на оператора -------------------------

    #Подбор, вакансии
    state: Operator-G05-vac || noContext=true
        script:
            sleep();
        a:  — Подключаю оператора —
            <ul>
                <li>для возврата к виртуальному помощнику введите любую из команд: /close, /bot, /закрыть чат, /вернуть бота.</li>
            </ul>
            Ожидайте, пожалуйста...
        go!: ./Switch

        state: Switch || noContext=true
            script:
                $context.session.ltxFirstMessage = "—————————————————" + "\n" + "ИСТОРИЯ ДИАЛОГА до переключения на оператора:" + "\n" + $context.client.chatHistory.map(function(val) {return val.type + "\n" + val.state + "\n" + val.text;}).join("\n\n") + "\n" + "—————————————————" + "\n" + "Рекомендации бота (тематики):" + "\n" + strSortNCount($context.session.chatHistorySTAT);
                $response.replies.push({
                    type:"switch",
                    destination: "G05-vac",
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
                    lastMessage: "— Диалог завершён —",
                    attributes: {
                        "Рекомендации бота (тематики)": strSortNCount($context.session.chatHistorySTAT),
                    }
                });

# ------------------------- Переключение на оператора -------------------------

    #Секретари
    state: Operator-G06-sec || noContext=true
        script:
            sleep();
        a:  — Подключаю оператора —
            <ul>
                <li>для возврата к виртуальному помощнику введите любую из команд: /close, /bot, /закрыть чат, /вернуть бота.</li>
            </ul>
            Ожидайте, пожалуйста...
        go!: ./Switch

        state: Switch || noContext=true
            script:
                $context.session.ltxFirstMessage = "—————————————————" + "\n" + "ИСТОРИЯ ДИАЛОГА до переключения на оператора:" + "\n" + $context.client.chatHistory.map(function(val) {return val.type + "\n" + val.state + "\n" + val.text;}).join("\n\n") + "\n" + "—————————————————" + "\n" + "Рекомендации бота (тематики):" + "\n" + strSortNCount($context.session.chatHistorySTAT);
                $response.replies.push({
                    type:"switch",
                    destination: "G06-sec",
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
                    lastMessage: "— Диалог завершён —",
                    attributes: {
                        "Рекомендации бота (тематики)": strSortNCount($context.session.chatHistorySTAT),
                    }
                });

