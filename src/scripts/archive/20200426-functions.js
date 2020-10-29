//Функция обновления информаци о клиенте, поступающей из входящего канала
function updateClientInfo() {
    var $client = $jsapi.context().client;
    var $session = $jsapi.context().session;
    var $parseTree = $jsapi.context().parseTree;
    var $start = $jsapi.context().session.start;
    
    //Обновление информации, полученной с портала time.hoff.ru
    //Инфо о страничке, с которой сотрудник обратился в чат 
    if (typeof $start !== 'undefined') {
        if (typeof $start.hofftime !== 'undefined') {
            if ($start.hofftime.url)
                {
                    $session.clientDestUrl = $start.hofftime.url;
                }
            //инфо о cотруднике
            if (typeof $start.hofftime.user !== 'undefined')
                {
                    //Обновление информации
                    if ($start.hofftime.user.email) {
                        $client.email = $start.hofftime.user.email;
                    } else if ($start.hofftime.user.lastName) {
                        $client.lastName = $start.hofftime.user.lastName;
                    } else if ($start.hofftime.user.name) {
                        $client.name = $start.hofftime.user.name;
                    } else if ($start.hofftime.user.middleName) {
                        $client.middleName = $start.hofftime.user.middleName;
                    } else if ($start.hofftime.user.tabNomer) {
                        $client.tabNomer = $start.hofftime.user.tabNomer;
                    } else if ($start.hofftime.user.unit) {
                        $client.unit = $start.hofftime.user.unit;
                    } else if ($start.hofftime.user.department) {
                        $client.department = $start.hofftime.user.department;
                    } else if ($start.hofftime.user.position) {
                        $client.position = $start.hofftime.user.position;
                    }
                
                    //Инфо о непосредственном руководителе сотрудника
                    if (typeof $start.hofftime.user.manager !== 'undefined')
                        {
                            //Создание объекта, если он не существет
                            if (typeof $client.manager === 'undefined') {
                                $client.manager = new Object();
                            }
                            //Обновление информации
                            if ($start.hofftime.user.manager.email) {   
                                $client.manager.email = $start.hofftime.user.manager.email;
                            } else if ($start.hofftime.user.manager.name) {
                                $client.manager.name = $start.hofftime.user.manager.name;
                            } else if ($start.hofftime.user.manager.lastName) {
                                $client.manager.lastName = $start.hofftime.user.manager.lastName;
                            } else if ($start.hofftime.user.manager.middleName) {
                                $client.manager.middleName = $start.hofftime.user.manager.middleName;
                            } else if ($start.hofftime.user.manager.tabNomer) {
                                $client.manager.tabNomer = $start.hofftime.user.manager.tabNomer;
                            } else if ($start.hofftime.user.manager.unit) {
                                $client.manager.unit = $start.hofftime.user.manager.unit;
                            } else if ($start.hofftime.user.manager.department) {
                                $client.manager.department = $start.hofftime.user.manager.department;
                            } else if ($start.hofftime.user.manager.position) {
                                $client.manager.position = $start.hofftime.user.manager.position;
                            }
                        }
                }
            if (typeof $client !== 'undefined')
                {
                    $jsapi.context().client = $client;
                }
        }
    }
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
    var toRemove = ["ИОБ","ИКО","ИТО","ИУО","ИУД","ИУС","ИУК","ЛОБ","ЛКБ","ЗОБ","ЗСТ","ЗСГ","ЗИМ","СОБ","СНА","СКО","ПОБ","КОБ","КВЫ","КНА","КНЕ","МОБ","МБО","МНД","МНЦ","МТТ"];
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
    str = typeof str !== "undefined" ? str : "/";
    //$reactions.answer(str);
    var arr = str.split("/");

    //Очищаем ctx.session.smartSwitchLastState после срабатывания, чтобы в врамках конкретной сессии не давало ложных срабатываний
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
                            "КОТ-01", "КОТ-02"];
    var toTransitG03edu = [
                            "КНА-05", "КНА-06", "КНА-07", "КНА-08",
                            "КОБ-05", "КОБ-10", "КОБ-09", "КОБ-07"];
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
                            "КДС-04", "КДС-05", "КДС-06", "КДС-07", "КДС-08", "КДС-09"];

    // Задаём переменную для передачи истории переписки
    //$context.session.ltxFirstMessage = "—————————————————" + "\n" + "ИСТОРИЯ ДИАЛОГА до переключения на оператора:" + "\n" + $context.client.chatHistory.map(function(val) {return val.type + "\n" + val.state + "\n" + val.text;}).join("\n\n") + "\n" + "—————————————————" + "\n" + "Рекомендации бота (тематики):" + "\n" + strSortNCount($context.session.chatHistorySTAT);

    $session.ltxFirstMessage = "—————————————————" + "\n" + "ИСТОРИЯ ДИАЛОГА до переключения на оператора:" + "\n" + $client.chatHistory.map(function(val) {return val.type + "\n" + val.state + "\n" + val.text;}).join("\n\n") + "\n" + "—————————————————" + "\n" + "Рекомендации бота (тематики):" + "\n" + strSortNCount($session.chatHistorySTAT);

    // задаем переменную для переключения по умолчанию
    // (переключает на все группы сразу), т.е. параметр destination не задан
    var smartSwitchGroupAll = {
        type:"switch",
        appendCloseChatButton: true,
        closeChatPhrases: [ "Вернуться к боту",
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
        attributes: {
            "Рекомендации бота (тематики)": strSortNCount($session.chatHistorySTAT),
        }
    };

    // Группа | G20-sup | Суперпользователи
    var smartSwitchGroupG20sup = {
        type:"switch",
        destination: "G20-sup",
        appendCloseChatButton: true,
        closeChatPhrases: [ "Вернуться к боту",
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
        attributes: {
            "Рекомендации бота (тематики)": strSortNCount($session.chatHistorySTAT),
        }
    };
    
    // Группа | G01-zrp | Зарплата
    var smartSwitchGroupG01zrp = {
        type:"switch",
        destination: "G01-zrp",
        appendCloseChatButton: true,
        closeChatPhrases: [ "Вернуться к боту",
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
        attributes: {
            "Рекомендации бота (тематики)": strSortNCount($session.chatHistorySTAT),
        }
    };

    // Группа | G02-kad | Кадрового администрирования
    var smartSwitchGroupG02kad = {
        type:"switch",
        destination: "G02-kad",
        appendCloseChatButton: true,
        closeChatPhrases: [ "Вернуться к боту",
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
        attributes: {
            "Рекомендации бота (тематики)": strSortNCount($session.chatHistorySTAT),
        } 
    };

    // Группа | G03-edu | Обучение и наставничество 
    var smartSwitchGroupG03edu = {
        type:"switch",
        destination: "G03-edu",
        appendCloseChatButton: true,
        closeChatPhrases: [ "Вернуться к боту",
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
        attributes: {
            "Рекомендации бота (тематики)": strSortNCount($session.chatHistorySTAT),
        }
    };

    // Группа | G04-mot | Мотивация 
    var smartSwitchGroupG04mot = {
        type:"switch",
        destination: "G04-mot",
        appendCloseChatButton: true,
        closeChatPhrases: [ "Вернуться к боту",
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
        attributes: {
            "Рекомендации бота (тематики)": strSortNCount($session.chatHistorySTAT),
        }
    };

    // Группа | G05-vac | Найм и вакансии
    var smartSwitchGroupG05vac = {
        type:"switch",
        destination: "G05-vac",
        appendCloseChatButton: true,
        closeChatPhrases: [ "Вернуться к боту",
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
        attributes: {
            "Рекомендации бота (тематики)": strSortNCount($session.chatHistorySTAT),
        }
    };

    // Группа | G06-sec | Секретари
    var smartSwitchGroupG06sec = {
        type:"switch",
        destination: "G06-sec",
        appendCloseChatButton: true,
        closeChatPhrases: [ "Вернуться к боту",
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
        attributes: {
            "Рекомендации бота (тематики)": strSortNCount($session.chatHistorySTAT),
        }
    };
    
    // Группа | G08-hax | AXHelpdesk
    var smartSwitchGroupG08hax = {
        type:"switch",
        destination: "G08-hax",
        appendCloseChatButton: true,
        closeChatPhrases: [ "Вернуться к боту",
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
        attributes: {
            "Рекомендации бота (тематики)": strSortNCount($session.chatHistorySTAT),
        }
    };
    
    //Правила переключения на группы операторов
    if (toTransitG01zrp.filter(function(n) {return arr.indexOf(n) !== -1;}).length > 0)
            {
                //G01zrp - Зарплата
                $session.smartSwitchlastGroup = "G01zrp";
                $reactions.answer('— Подключаю оператора — <ul><li>группа: зарплата.</li></ul>');
                $response.replies.push(smartSwitchGroupG01zrp);
            }
    else if (toTransitG02kad.filter(function(n) {return arr.indexOf(n) !== -1;}).length > 0)
            {
                //G02kad - Кадровое администрирование
                $session.smartSwitchlastGroup = "G02kad";
                $reactions.answer('— Подключаю оператора — <ul><li>группа: кадровое администрирование.</li></ul>');
                $response.replies.push(smartSwitchGroupG02kad);
            }
    else if (toTransitG03edu.filter(function(n) {return arr.indexOf(n) !== -1;}).length > 0)
            {
                //G03edu - Обучение и наставничество
                $session.smartSwitchlastGroup = "G03edu";
                $reactions.answer('— Подключаю оператора — <ul><li>группа: обучение и наставничество.</li></ul>');
                $response.replies.push(smartSwitchGroupG03edu);
            }
    else if (toTransitG04mot.filter(function(n) {return arr.indexOf(n) !== -1;}).length > 0)
            {
                //G04mot - Отдел мотивации
                $session.smartSwitchlastGroup = "G04mot";
                $reactions.answer('— Подключаю оператора — <ul><li>группа: отдел мотивации.</li></ul>');
                $response.replies.push(smartSwitchGroupG04mot);
            }
    else if (toTransitG05vac.filter(function(n) {return arr.indexOf(n) !== -1;}).length > 0)
            {
                //G05vac - Подбор и вакансии
                $session.smartSwitchlastGroup = "G05vac";
                $reactions.answer('— Подключаю оператора — <ul><li>группа: подбор и вакансии.</li></ul>');
                $response.replies.push(smartSwitchGroupG05vac);
            }
    else if (toTransitG06sec.filter(function(n) {return arr.indexOf(n) !== -1;}).length > 0)
            {
                //G06sec - Секретари
                $session.smartSwitchlastGroup = "G06sec";
                $reactions.answer('— Подключаю оператора — <ul><li>группа: секретари.</li></ul>');
                $response.replies.push(smartSwitchGroupG06sec);
            }
//    else if (toTransitG07hds.filter(function(n) {return arr.indexOf(n) !== -1;}).length > 0)
//            {
//                //G07hds - Helpdesk
//                $session.smartSwitch.lastGroup = "G07hds";
//                $reactions.answer('— Подключаю оператора — <ul><li>группа: helpdesk (тех. поддержка по общим вопросам).</li></ul>');
//                //$response.replies.push(smartSwitchGroupG07hds);
//            }
    else if (toTransitG08hax.filter(function(n) {return arr.indexOf(n) !== -1;}).length > 0)
            {
                //G08hax - AXHelpdesk
                $session.smartSwitchlastGroup = "G08hax";
                $reactions.answer('— Подключаю оператора — <ul><li>группа: axhelpdesk (тех. поддержка по копроративному порталу и биометрии).</li></ul>');
                $response.replies.push(smartSwitchGroupG08hax);
            }
    else
            {
                //G20sup - Cупероператоры
                $session.smartSwitchlastGroup = "G20sup";
                $reactions.answer('— Подключаю оператора — <ul><li>группа: общие вопросы.</li></ul>');
                $response.replies.push(smartSwitchGroupG20sup);
                //$session.smartSwitchlastGroup = "all";
                //$reactions.answer('— Подключаю оператора — <ul><li>группа: All - ОБЩИЕ.</li></ul>');
                //$response.replies.push(smartSwitchGroupAll);
            }
}