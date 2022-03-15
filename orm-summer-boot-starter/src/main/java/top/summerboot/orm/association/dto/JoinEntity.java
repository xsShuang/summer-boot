/*
 *
 * Copyright 2022 xieshuang(https://github.com/xsShuang/summer-boot)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.summerboot.orm.association.dto;

import lombok.Data;

/**
 * 关联对象
 *
 * @author xieshuang
 * @date 2020-11-13 17:10
 */
@Data
public class JoinEntity<E> {

    /**
     * 主信息id
     */
    private String mainId;

    /**
     * 关联对象
     */
    private E entity;

}
