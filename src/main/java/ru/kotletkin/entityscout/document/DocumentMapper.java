package ru.kotletkin.entityscout.document;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import ru.kotletkin.entityscout.document.model.TikaContent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DocumentMapper {

    public static TikaContent toTikaContent(Metadata metadata) {

        Map<String, String> metadataMap = new HashMap<>();

        String text = Optional.ofNullable(metadata.get(TikaCoreProperties.TIKA_CONTENT)).orElse("");
        String resourceName = Optional.ofNullable(metadata.get(TikaCoreProperties.RESOURCE_NAME_KEY)).orElse("");
        String title = Optional.ofNullable(metadata.get(TikaCoreProperties.TITLE)).orElse("");
        String contentType = Optional.ofNullable(metadata.get(Metadata.CONTENT_TYPE)).orElse("");
        String isEncrypted = Optional.ofNullable(metadata.get(TikaCoreProperties.IS_ENCRYPTED)).orElse("false");

        String[] metadataKeys = metadata.names();
        for (String metaKey : metadataKeys) {
            if (!metaKey.contains("X-TIKA")) {
                metadataMap.put(metaKey, metadata.get(metaKey));
            }
        }

        return new TikaContent(resourceName, title, contentType, text, isEncrypted, metadataMap);
    }

    public static List<TikaContent> toTikaContent(List<Metadata> metadataList) {
        return metadataList.stream().map(DocumentMapper::toTikaContent).toList();
    }
}
