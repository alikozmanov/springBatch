package fr.fms.processors;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class TextItemProcessor implements ItemProcessor<String,String> {

    @Override
    public String process(String string) throws Exception {
        return string.replaceAll("\\d", "*");
    }
}
