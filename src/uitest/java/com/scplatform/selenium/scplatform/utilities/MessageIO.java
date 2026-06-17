/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.utilities;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.joda.time.DateTime;

import com.scplatform.qa.iris.model.MessageLine;
import com.scplatform.qa.iris.model.MessageLineStructure;
import com.scplatform.qa.iris.model.proxy.MessageLineProxy;
import com.scplatform.qa.iris.serialization.json.JSONDeserializer;
import com.scplatform.qa.iris.serialization.json.JSONSerializer;
import com.test.selenium.common.AbstractPage;
import com.test.selenium.common.Configuration;
import com.test.selenium.common.JLog;
import com.test.selenium.common.RealTime;
import com.test.selenium.scplatform.messages.forecast.Forecast;
import com.test.selenium.scplatform.messages.forecast.ForecastBuilder;
import com.test.selenium.scplatform.messages.item.Item;
import com.test.selenium.scplatform.messages.item.ItemBuilder;
import com.test.selenium.scplatform.messages.sourcingLane.SourcingLane;
import com.test.selenium.scplatform.messages.sourcingLane.SourcingLaneBuilder;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

public class MessageIO<T extends MessageLine> {

    private Class<T> messageClazz = null;

    public MessageIO(Class<T> messageClazz) {
        this.messageClazz = messageClazz;
    }

    private String defaultKey() {
        return this.messageClazz.getSimpleName() + ".saved";
    }

    private String getActualKey(String key) {
        if (key.startsWith(this.messageClazz.getName())) {
            return key;
        }
        return this.messageClazz.getSimpleName() + "." + key;
    }

    /**
     * Saves Iterable message lines to a JSON file.<br>
     * The file is saved with a default key of messageClazz.getName() +
     * ".saved"<br>
     * Use in conjunction with {@link #load()}
     * 
     * @param messageLines
     *            Iterable message lines
     * 
     * @see #save(Iterable, String)
     * @see #load()
     * @see #load(String)
     */
    public List<T> save(List<T> messageLines) {
        return save(messageLines, defaultKey());
    }

    /**
     * Saves Iterable message lines to a JSON file.<br>
     * Use in conjunction with {@link #load(String)}
     * 
     * @param messageLines
     *            Iterable message lines
     * @param saveToKey
     *            The key to save to
     * 
     * @see #save(Iterable)
     * @see #load()
     * @see #load(String)
     */
    @SuppressWarnings("unchecked")
    public List<T> save(List<T> messageLines, String saveToKey) {
        Preconditions.checkNotNull(messageClazz, "Message class cannot be NULL!");
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        try {
            JSONSerializer js = new JSONSerializer();
            MessageLineStructure<T> mls = (MessageLineStructure<T>) MessageLineProxy.newInstance(messageClazz)
                    .getMessageStructure();
            js.serialize(messageLines, mls, os);
            os.close();

            File jsonFile = makeFile();
            FileOutputStream outputStream = new FileOutputStream(jsonFile);
            os.writeTo(outputStream);

            Configuration.setRuntime(getActualKey(saveToKey), jsonFile.getPath());
        } catch (IOException e) {
            JLog.error(e);
        }

        return Lists.newArrayList(messageLines);
    }

    /**
     * Determines if a message was previously saved.
     * 
     * @param saveKey
     *            The key it was saved under, this can be null.
     * @return true if the key exists in the runtime file
     */
    public boolean doesMessageExist(String saveKey) {
        Preconditions.checkNotNull(messageClazz, "Message class cannot be NULL!");
        return Configuration.runtimeContainsKey(getActualKey(saveKey));
    }

    /**
     * Loads a JSON file to a Iterable of MessageLines using the default save
     * key (see {@link #save(Iterable)})
     * 
     * @return Iterable or the MessageLines. Null if nothing found.
     * 
     * @see #load(String)
     */
    public List<T> load() {
        return load(defaultKey());
    }

    /**
     * Loads a JSON file to a Iterable of MessageLines
     * 
     * @param saveKey
     *            The key that the data was saved under (see
     *            {@link #save(Iterable)} and {@link #save(Iterable, String)})
     * @return Iterable or the MessageLines. Null if nothing found.
     * 
     * @see #load()
     */
    public List<T> load(String saveKey) {
        Preconditions.checkNotNull(messageClazz, "Message class cannot be NULL!");
        String actualKey = getActualKey(saveKey);

        String savedFile = Configuration.getRuntime(actualKey);
        Preconditions.checkNotNull(savedFile, "No data found using key: " + actualKey);

        File file = new File(savedFile);
        Preconditions.checkArgument(file.exists(), String.format(
                "Found file for key '%s' as '%s',  but the file does not actually exist!", actualKey, savedFile));

        Iterable<T> deserialize = null;

        try {
            JSONDeserializer jDeserializer = new JSONDeserializer();
            if (messageClazz.isAssignableFrom(Item.class)) {
                jDeserializer.addListClass(ItemBuilder.getSubClasses());
            } else if (messageClazz.isAssignableFrom(SourcingLane.class)) {
                jDeserializer.addListClass(SourcingLaneBuilder.getSubClasses());
            } else if (messageClazz.isAssignableFrom(Forecast.class)) {
                jDeserializer.addListClass(ForecastBuilder.getSubClasses());
            }
            deserialize = jDeserializer.deserialize(new FileInputStream(file), messageClazz);
        } catch (FileNotFoundException e) {
            JLog.error(e);
        }

        return Lists.newArrayList(deserialize);
    }

    private File makeFile() {
        AbstractPage helper = new AbstractPage();
        String unique = helper.getUniqueNumber();
        RealTime runtime = RealTime.getInstance();
        String workingDir = runtime.getWorkingDir();

        String saveToFile = workingDir + this.messageClazz.getName() + "-" + unique + ".json";

        return new File(saveToFile);
    }

    public DateTime getSaveDate(String savedRuntimeKey) {
        String fileName = Configuration.getRuntime(savedRuntimeKey);

        int dateStart = fileName.lastIndexOf('_') + 1;
        int dateEnd = fileName.lastIndexOf('.') - 3;

        AbstractPage helper = new AbstractPage();
        return new DateTime(helper.getDateFromUniqueNumber(fileName.substring(dateStart, dateEnd)));
    }
}
