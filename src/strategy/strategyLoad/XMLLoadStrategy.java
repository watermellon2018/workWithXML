package strategy.strategyLoad;

import data.RecordDepartament;
import dataBase.DBConnection;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class XMLLoadStrategy implements StrategyLoad {

    private DocumentBuilder builder;
    private static final Logger xmlLoadStrLogger = LogManager.getLogger(XMLLoadStrategy.class);


    public XMLLoadStrategy(){
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            builder = factory.newDocumentBuilder();
            xmlLoadStrLogger.debug("Получиил билдер для чтения xml файла");

        } catch (ParserConfigurationException e) {
            xmlLoadStrLogger.error(e);
        }
    }


    /**
     *  Проходимся по файлу и создаем объекты на основе данных, которые находятся в базе
     *  **/

    @Override
    public Map<Integer, Set<RecordDepartament>> load(Set<RecordDepartament> data, File file) {
        Set<RecordDepartament> dataInFile = new HashSet<>();

        try {
            Document document = builder.parse(file);
            NodeList departamentElements = document.getDocumentElement().getElementsByTagName("Departament");

            xmlLoadStrLogger.debug("Получили список node департаментов, будем вытаскивать информацию");

            for (int i = 0; i < departamentElements.getLength(); i++) {

                Node departament = departamentElements.item(i);
                NodeList attList = departament.getChildNodes();
                String[] attDepart = new String[3];

                /**Собираем объект**/
                for (int j = 0; j < attList.getLength(); j++) {

                    Node item = attList.item(j);
                    attDepart[j] = item.getTextContent();
                }

                dataInFile.add(new RecordDepartament(attDepart[0], attDepart[1], attDepart[2]));
                xmlLoadStrLogger.debug("Добавили объект в список");

            }

            if(dataInFile.size() != departamentElements.getLength())
                throw new Exception();

            xmlLoadStrLogger.debug("Загрузка прошла успешно, у нас нет записей с одинаковыми ключами");

            Map<Integer, Set<RecordDepartament>> res = doSynh(data, dataInFile);
            return res;

        } catch (SAXException e) {
            xmlLoadStrLogger.error(e);
        } catch (IOException e){
            xmlLoadStrLogger.error(e);
            System.out.println("Что - то не так с входными файлами. \nПроверьте имя файла");
        } catch (Exception e) {
            xmlLoadStrLogger.error(e);
            System.out.println("В файле есть две записи с одинаковыми натуральными ключами");
        }

        throw new NullPointerException();
    }

    /**
     * Нужно оставить только те записи, с которыми придется образаться к базе данных
     * Два множества (БАЗА и ФАЙЛ) - данные из базы и данные из файла
     * Сначала удаляем одинаковые (которые находятся и там, и там), они остаются не тронутые в базе
     * Записи с одинаковыми натуральными ключами, но разными комментариями должны быть обновлены в базе
     * Оставшиеся записи в множестве БАЗА удаляем, а в ФАЙЛе добавляем в базу данных
     * Результат метода ассоциативные массив - записи для: удалаения, добавления и обновления
     * **/

    private Map<Integer, Set<RecordDepartament>> doSynh(Set<RecordDepartament> dataInBase, Set<RecordDepartament> dataInFile){

        Map<Integer, Set<RecordDepartament>> result = new HashMap<>();
        Set<RecordDepartament> updateItem = new HashSet<>();
        Iterator<RecordDepartament> iterFile =  dataInFile.iterator();

        while (iterFile.hasNext()){

            RecordDepartament fileItem = iterFile.next();
            Iterator<RecordDepartament> iterBase = dataInBase.iterator();

            while (iterBase.hasNext()){

                RecordDepartament baseItem = iterBase.next();

                if(baseItem.equals(fileItem)) {
                    iterFile.remove();
                    iterBase.remove();

                    break;
                }else if(baseItem.isUpdating(fileItem)) {
                    updateItem.add(fileItem);
                    iterFile.remove();
                    iterBase.remove();
                    break;
                }
            }
        }

        xmlLoadStrLogger.debug("Закончили распределение по множествам записей (для удаления, добавления и обновления)");

        result.put(DBConnection.ADD, dataInFile);
        result.put(DBConnection.DELETE, dataInBase);
        result.put(DBConnection.UPDATE, updateItem);

        xmlLoadStrLogger.debug("Сформировали ответ для множеств");

        return result;
    }
}