package cn.rongcapital.chorus.modules.email_notify;

import org.springframework.stereotype.Component;

import com.github.rjeschke.txtmark.Processor;

@Component
public class MarkdownConverter implements Converter {
    
    @Override
    public String convert(String markdown){
        return Processor.process(markdown);
    }
}
