package org.spoorn.myloot.config;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Data
public class BlockMapping {
    
    public String myLootType;
    public List<String> replaces;
}
