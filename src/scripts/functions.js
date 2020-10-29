//Функция инициализирует имя бота и позволяет вставлять его с учетом склонений по падежам
//function botName(str) {
//    var $session = $jsapi.context().session;
//    
//    $session.botNameIP = "Ииигорь";   //# имя бота в именительном (Кто? Что?)
//    $session.botNameRP = "Ииигоря";   //# имя бота в родительном  (Кого? Чего?)
//    $session.botNameDP = "Ииигорю";   //# имя бота в дательном    (Кому? Чему?)
//    $session.botNameVP = "Ииигоря";   //# имя бота в винительном  (Кого? Что?)
//    $session.botNameTP = "Ииигорем";  //# имя бота в творительном (Кем? Чем?)
//    $session.botNamePP = "Ииигоре";   //# имя бота в предложном   (О ком? О чем?)
//    $session.botNameStart = "Меня зовут Ииигорь. ";   //# имя бота в приветственном сообщении.
//    
//    if (str == "IP" || "ip")        { return $session.botNameIP;
//    } else if (str == "RP" || "rp") { return $session.botNameRP;
//    } else if (str == "DP" || "dp") { return $session.botNameDP;
//    } else if (str == "VP" || "vp") { return $session.botNameVP;
//    } else if (str == "TP" || "tp") { return $session.botNameTP;
//    } else if (str == "PP" || "pp") { return $session.botNamePP;
//    } else if (str == "Start" || "start") { return $session.botNameStart;
//    } else { 
//        return "";
//    };
//}

//Функция обновления информации о клиенте, поступающей из входящего канала
function updateClientInfo() {
    var $client = $jsapi.context().client;
    var $session = $jsapi.context().session;
    var $parseTree = $jsapi.context().parseTree;
    
    //удаляем хвосты от устаревших скриптов
    //delete $client.hofftime;
    //delete $client.start;
    //delete $client.manager;
    //delete $client.cameFromUrl;
    //delete $client.employee;
    //delete $client.name;
	//delete $client.lastName;
	//delete $client.middleName;
	//delete $client.tabNomer;
	//delete $client.unit;
	//delete $client.department;
	//delete $client.position;
    
    // создаем предустановленные по умолчанию объекты в $client, если они не существуют
    $client.user   = $client.user               || {}; // для данных пользователя (основных)
    $client.ch     = $client.ch                 || {}; // для данных пользователя (из каналов)
    
    // проверяем, что вместе с командой "/start" передан объект с данными "/start {}" и заносим его в $session.start
    if ($parseTree.text.indexOf("{") != -1) {
    	try {
            $session.start = JSON.parse($parseTree.text.substring($parseTree.text.indexOf("{"), $parseTree.text.length)); //.replace('\"', '"')
        } catch(e) {
            log("ERROR. Start data is not JSON object.");
        }
    }
    
    //проверяем, что можем приступать к обновлению глобальных объектов, переданных из $session.start
    //здесь мы создаем: $client.ch.hofftime
    if (!$session.start) {
        log("ERROR. $session.start does not exist (null or undefined). Maybe start data is not JSON object.");
    } else {   
        
        // заполняем данные $client.ch.hofftime, если $session.start.hofftime существует
        if ((($session || {}).start || {}).hofftime) {
            $client.ch.hofftime = $session.start.hofftime;
        }
    }
    
    //проверяем, что можем приступать к обновлению локальных переменных из $session.start
    //здесь мы создаем: $client.user.name и $client.user.email
    if (!$session.start) {
        log("ERROR. $session.start does not exist (null or undefined). Maybe start data is not JSON object.");
    } else {   
        
        // обновляем ИМЯ пользователя в $client.user.name, если $session.start.hofftime.user.name существует
        if ((((($session || {}).start || {}).hofftime || {}).user || {}).name) {
            $client.user.name = $session.start.hofftime.user.name;
        }
        
        // обновляем EMAIL пользователя в $client.user.email, если $session.start.hofftime.user.email существует
        if ((((($session || {}).start || {}).hofftime || {}).user || {}).email) {
            $client.user.email = $session.start.hofftime.user.email.toLowerCase();
        }
    }    

        // обеновляем информацию    
        //switch (true) {
        //    
        //    // если объекта не было и тем самым на его основе не создана переменная $start, то пишем, что переданные данные не являются объектом и прерываем выполнение switch
        //    case !$session.start:                                   log("ERROR! Start data non JSON object."); break;
        //        
        //    // если же все норм, то сначала создаем недостающие объекты в $client
        //    case $session.start.hofftime.user:      
        //        $client.user = new Object();
        //        $client.user.openChatUrl = $session.start.hofftime.url
        //        $client.user.email       = $session.start.hofftime.user.email || "";
        //        $client.user.name        = $session.start.hofftime.user.name || "";
        //        $client.user.lastName    = $session.start.hofftime.user.lastName || "";
        //        $client.user.middleName  = $session.start.hofftime.user.middleName || "";
        //        $client.user.tabNomer    = $session.start.hofftime.user.tabNomer || "";
        //        $client.user.unit        = $session.start.hofftime.user.unit || "";
        //        $client.user.department  = $session.start.hofftime.user.department || "";
        //        $client.user.position    = $session.start.hofftime.user.position || "";
        //        break; 
        //    case $session.start.hofftime.user.manager:              
        //        $client.manager = new Object();
        //        $client.manager.email       = $session.start.hofftime.user.manager.email || "";
        //        $client.manager.name        = $session.start.hofftime.user.manager.name || "";
        //        $client.manager.lastName    = $session.start.hofftime.user.manager.lastName || "";
        //        $client.manager.middleName  = $session.start.hofftime.user.manager.middleName || "";
        //        $client.manager.tabNomer    = $session.start.hofftime.user.manager.tabNomer || "";
        //        $client.manager.unit        = $session.start.hofftime.user.manager.unit || "";
        //        $client.manager.department  = $session.start.hofftime.user.manager.department || "";
        //        $client.manager.position    = $session.start.hofftime.user.manager.position || "";
        //        break; 
        //}    
}

//Функция 
function getcity() {
    var $session = $jsapi.context().session;
    var $parseTree = $jsapi.context().parseTree;
    $session.ptrnCity = $parseTree._ptrnCity.name;
}

//Функция для таймаута ответа бота в милисекундах, со значением по умолчанию в 1500 миллисекунд
function sleep(milliseconds) { 
    milliseconds = typeof milliseconds !== "undefined" ? milliseconds : "1500";
    var timeStart = new Date().getTime(); 
    while (true) { 
        var elapsedTime = new Date().getTime() - timeStart; 
        if (elapsedTime > milliseconds) { 
            break; 
        } 
    } 
}

//Функция универсальная для запроса на веб-сервис, запускается в сценарии по скрипту var $httpResponse = getStatus();
//перед запуском требует в сценарии определения переменных $session.wsUrl, $session.wsBody, $session.wsTransGood, $session.wsTransBad
function getStatus() {
    var $session = $jsapi.context().session;
    var url = $session.wsUrl;
    var options = {
        dataType: "json",
        method: $session.wsMethod,
        headers: {},
        body: $session.wsBody
    };
    var result = $http.post(url, options);
    if (result.isOk && result.status >= 200 && result.status < 300) {
        return result.data;
    } else {
        $reactions.transition($session.wsTransBad);
    }
}

//Функция перебора элементов в массиве объектов (myArray) для поиска позиции объекта с известным именем (property) и значением (searchTerm)
function arrayObjectIndexOf(myArray, property, searchTerm) {
    for(var i = 0, len = myArray.length; i < len; i++) {
        if (myArray[i][property] === searchTerm) {
            return i;
        }
    }
    return -1;
}

//Функция подсчета и сортировки повторяющихся элементов в строке "str", разбиваемой в массив символом "/"
function strSortNCount(str) {
    str = typeof str !== "undefined" ? str : "/";                   //на случай ошибки (TypeError: Cannot read property "split" from undefined)
    var arr = str.split("/");                                       //делим строку, содержащую полный путь стейта по разделителю в кавычках
    for (var len = arr.length, i = len; --i >= 0;) {                //объединяем повторяющиеся элементы массива и считаем их частотность
      if (arr[arr[i]]) {
        arr[arr[i]] += 1;
        arr.splice(i, 1);
      } else {
        arr[arr[i]] = 1;
      }
    }
    arr.sort(function(a, b) {                                       //сортируем элементы массива по убыванию их частотности
      return arr[b] - arr[a];
    });                                                             //задаем массив с элементами, по которым будет фильтрация
    var toRemove = [
                    "ИОБ", "ИКО", "ИТО", "ИУО", "ИУД", "ИУС", "ИУК",
                    "ЛОБ", "ЛКБ",
                    "ЗОБ", "ЗСТ", "ЗСГ", "ЗИМ",
                    "СОБ", "СНА", "СКО",
                    "ПОБ",
                    "КОБ", "КДС", "КМЕ", "КПО", "КВЫ", "КНА", "КАО", "КРА", "КОТ",
                    "МОБ", "МБО", "МНД", "МНЦ", "МТТ"];
    arr = arr.filter(function(x) {                                  //оставляем в массиве только те элементы, которые перечислены в массиве toRemove
        return toRemove.indexOf(x) !== -1;                           
    });
//    var stringResult = JSON.stringify(arr, function(k, v) {       //выводим названия стейтов и их частотность с переносом на следующую строку
//      if (k === '') return v;
//      return `${arr[v]} - ${v}`;
//    },1);
    var stringResult = arr.join(", ");                              //выводим названия стейтов в виде строки без переноса на следующую строку
    return stringResult;
}


//Функция переключения на нужную группу операторов
function smartSwitch(str) {
    var $client = $jsapi.context().client;
    var $session = $jsapi.context().session;
    var $response = $jsapi.context().response;
    
    $session.smartSwitchCheck = true;
    //$session.smartSwitchCount = typeof $session.smartSwitchCount !== "undefined" ? $session.smartSwitchCount : 0;
    //++$session.smartSwitchCount;
    
    str = typeof str !== "undefined" ? str : "/";
    //$reactions.answer(str);
    var arr = str.split("/");

    // Очищаем ctx.session.smartSwitchLastState после срабатывания, чтобы в врамках конкретной сессии не давало ложных срабатываний
    $session.smartSwitchLastState = undefined;
    
    // Задаём правила фильтрации, из каких (вопросов/стейтов) необходимо сделать переход на конкретную группу операторов

    var toTransitG01zrp = [
                            "КВЫ-01", "КВЫ-08", "КВЫ-11"];
    var toTransitG02kad = [
                            "КВЫ-02", "КВЫ-03", "КВЫ-04", "КВЫ-05", "КВЫ-06", "КВЫ-07", "КВЫ-09", "КВЫ-10", "КВЫ-12", "КВЫ-13", "КВЫ-14", "КВЫ-15",
                            "КДЕ-02", "КДЕ-05", "КДЕ-06", "КДЕ-07",
                            "КМЕ-01", "КМЕ-06", "КМЕ-07",
                            "КНА-02", "КНА-03",
                            "КОБ-01", "КОБ-02", "КОБ-06",
                            "КОТ-01", "КОТ-02", "КОТ-03", "КОТ-04", "КОТ-05", "КОТ-06"];
    var toTransitG03edu = [
                            "КАО-01", "КАО-02", "КАО-03", "КАО-04", "КАО-05", "КАО-06", "КАО-07", "КАО-08", "КАО-09", "КАО-10",
                            "КАО-11", "КАО-12", "КАО-13", "КАО-14",
                            "КОБ-07"];
    var toTransitG04mot = [];
    var toTransitG05vac = [
                            "КНА-01", "КНА-04"];
    var toTransitG06sec = [
                            "КДЕ-01", "КДЕ-03", "КДЕ-04",
                            "КДС-02", "КДС-03",
                            "КМЕ-02", "КМЕ-03", "КМЕ-04", "КМЕ-05", "КМЕ-08", "КМЕ-09", "КМЕ-10", "КМЕ-11", "КМЕ-12",
                            "КОБ-03", "КОБ-04", "КОБ-08",
                            "КРА-01", "КРА-02", "КРА-03", "КРА-04", "КРА-05", "КРА-06", "КРА-07", "КРА-08"];
    var toTransitG07hds = [
                            "КДС-01"];
    var toTransitG08hax = [
                            "КДС-04", "КДС-05", "КДС-06", "КДС-07", "КДС-08", "КДС-09",
                            "КОБ-10"];
    var toTransitG09cor = [
                            "КПО-01", "КПО-02", "КПО-03", "КПО-04", "КПО-05", "КПО-06",
                            "КОБ-09", "КОБ-10", "КОБ-11", "КОБ-12", "КОБ-13", "КОБ-14"];
    var toTransitG21tst = [
                            "infotest"];

    // Задаём переменную для передачи истории переписки
    // $context.session.ltxFirstMessage = "—————————————————" + "\n" + "ИСТОРИЯ ДИАЛОГА до переключения на оператора:" + "\n" + $context.client.chatHistory.map(function(val) {return val.type + "\n" + val.state + "\n" + val.text;}).join("\n\n") + "\n" + "—————————————————" + "\n" + "Рекомендации бота (тематики):" + "\n" + strSortNCount($context.session.chatHistorySTAT);

    $session.ltxFirstMessage = "—————————————————" + "\n" + "ИСТОРИЯ ДИАЛОГА до переключения на оператора:" + "\n" + $client.chatHistory.map(function(val) {return val.type + "\n" + val.state + "\n" + val.text;}).join("\n\n") + "\n" + "—————————————————" + "\n" + "Рекомендации бота (тематики):" + "\n" + strSortNCount($session.chatHistorySTAT);

    // задаем переменную, передающую атрибуты в операторский чат
    if ((($client || {}).ch || {}).hofftime
    && ((($client || {}).ch || {}).hofftime || {}).user) {
        $session.smartSwitchAttributes = {
                "1) url":               $client.ch.hofftime.url,
                "2) сотрудник":         $client.ch.hofftime.user.tabNomer+" "+$client.ch.hofftime.user.name+" "+$client.ch.hofftime.user.middleName+" "+$client.ch.hofftime.user.lastName,
                "3) бю, должность":     $client.ch.hofftime.user.unit+" "+$client.ch.hofftime.user.position,
                "4) email":             $client.ch.hofftime.user.email,
        };
    } else {
        $session.smartSwitchAttributes = {
                "1) url":               "",
                "2) сотрудник":         "",
                "3) бю, должность":     "",
                "4) email":             "",
        };
    };
    
    // Задаем саму переменную для переключения по умолчанию
    // Избирательно переключает на указанную группу операторов
    // Если destination будет пустым, то переключит по умолчанию (на всех сразу)
    // Первую команду их closeChatPhrases подставляет в кнопку если appendCloseChatButton = true когда оператор отправит хотя бы одну фразу

    
    var smartSwitchGroup = {
        type:"switch",
        destination: $session.smartSwitchlastGroup,
        appendCloseChatButton: true,
        closeChatPhrases: [ "Вернуться к виртуальному помощнику Ииигорю",
                            "/close", "/сдщыу",
                            "/bot", "/ище","/бот", "/,jn",
                            "/закрыть чат", "/pfrhsnm xfn", "/закройте чат", "/pfrhjqnt xfn", "/закрой чат", "/pfrh* xfn",
                            "/вернуть бота", "/dthyenm ,jnf", "/верните бота", "/dthybnt ,jnf", "/верни бота", "/dthyb ,jnf",
                            "\close", "\сдщыу",
                            "\bot", "\ище","\бот", "\,jn",
                            "\закрыть чат", "\pfrhsnm xfn", "\закройте чат", "\pfrhjqnt xfn", "\закрой чат", "\pfrh* xfn",
                            "\вернуть бота", "\dthyenm ,jnf", "\верните бота", "\dthybnt ,jnf", "\верни бота", "\dthyb ,jnf",
                            "закрыть чат", "pfrhsnm xfn", "закройте чат", "pfrhjqnt xfn", "закрой чат", "pfrh* xfn",
                            "вернуть бота", "dthyenm ,jnf", "верните бота", "dthybnt ,jnf", "верни бота", "dthyb ,jnf"],
        firstMessage: $session.ltxFirstMessage,
        lastMessage: "— Собеседник покинул чат —",
        attributes: $session.smartSwitchAttributes
    };
    
    //Правила переключения на группы операторов
    if (toTransitG01zrp.filter(function(n) {return arr.indexOf(n) !== -1;}).length > 0)
            {
                //G01zrp - Зарплата
                $session.smartSwitchlastGroup = "G01zrp";
                $reactions.answer('— Подключаю оператора — <ul><li>группа: зарплата.</li></ul>');
                $response.replies.push(smartSwitchGroup);
            }
    else if (toTransitG02kad.filter(function(n) {return arr.indexOf(n) !== -1;}).length > 0)
            {
                //G02kad - Кадровое администрирование
                $session.smartSwitchlastGroup = "G02kad";
                $reactions.answer('— Подключаю оператора — <ul><li>группа: кадровое администрирование.</li></ul>');
                $response.replies.push(smartSwitchGroup);
            }
    else if (toTransitG03edu.filter(function(n) {return arr.indexOf(n) !== -1;}).length > 0)
            {
                //G03edu - Обучение и наставничество
                $session.smartSwitchlastGroup = "G03edu";
                $reactions.answer('— Подключаю оператора — <ul><li>группа: обучение и наставничество.</li></ul>');
                $response.replies.push(smartSwitchGroup);
            }
    else if (toTransitG04mot.filter(function(n) {return arr.indexOf(n) !== -1;}).length > 0)
            {
                //G04mot - Отдел мотивации
                $session.smartSwitchlastGroup = "G04mot";
                $reactions.answer('— Подключаю оператора — <ul><li>группа: отдел мотивации.</li></ul>');
                $response.replies.push(smartSwitchGroup);
            }
    else if (toTransitG05vac.filter(function(n) {return arr.indexOf(n) !== -1;}).length > 0)
            {
                //G05vac - Подбор и вакансии
                $session.smartSwitchlastGroup = "G05vac";
                $reactions.answer('— Подключаю оператора — <ul><li>группа: подбор и вакансии.</li></ul>');
                $response.replies.push(smartSwitchGroup);
            }
    else if (toTransitG06sec.filter(function(n) {return arr.indexOf(n) !== -1;}).length > 0)
            {
                //G06sec - Секретари
                $session.smartSwitchlastGroup = "G06sec";
                $reactions.answer('— Подключаю оператора — <ul><li>группа: секретари.</li></ul>');
                $response.replies.push(smartSwitchGroup);
            }
//    else if (toTransitG07hds.filter(function(n) {return arr.indexOf(n) !== -1;}).length > 0)
//            {
//                //G07hds - Helpdesk
//                $session.smartSwitch.lastGroup = "G07hds";
//                $reactions.answer('— Подключаю оператора — <ul><li>группа: helpdesk (тех. поддержка по общим вопросам).</li></ul>');
//                //$response.replies.push(smartSwitchGroup);
//            }
    else if (toTransitG08hax.filter(function(n) {return arr.indexOf(n) !== -1;}).length > 0)
            {
                //G08hax - AXHelpdesk
                $session.smartSwitchlastGroup = "G08hax";
                $reactions.answer('— Подключаю оператора — <ul><li>группа: axhelpdesk (техподдержка по биометрии).</li></ul>');
                $response.replies.push(smartSwitchGroup);
            }
    else if (toTransitG09cor.filter(function(n) {return arr.indexOf(n) !== -1;}).length > 0)
            {
                //G09cor - Корпоративный портал
                $session.smartSwitchlastGroup = "G09cor";
                $reactions.answer('— Подключаю оператора — <ul><li>группа: корпоративный портал (техподдержка по копроративному порталу).</li></ul>');
                $response.replies.push(smartSwitchGroup);
            }
    else if (toTransitG21tst.filter(function(n) {return arr.indexOf(n) !== -1;}).length > 0)
            {
                //G21tst - Test
                $session.smartSwitchlastGroup = "G21tst";
                $reactions.answer('— Подключаю оператора — <ul><li>группа: test.</li></ul>');
                $response.replies.push(smartSwitchGroup);
            }
    else
            {
                //G20sup - Cупероператоры
                $session.smartSwitchlastGroup = "G20sup";
                $reactions.answer('— Подключаю оператора — <ul><li>группа: общие вопросы.</li></ul>');
                $response.replies.push(smartSwitchGroup);
                //$session.smartSwitchlastGroup = "";
                //$reactions.answer('— Подключаю оператора — <ul><li>группа: All - ОБЩИЕ.</li></ul>');
                //$response.replies.push(smartSwitchGroup);
            }
}