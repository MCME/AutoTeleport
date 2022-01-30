# -*- coding: utf-8 -*-
"""
Created on Sun Jan 30 12:56:36 2022

@author: Julian
"""

import yaml

with open("PluginData.yml", 'r') as stream:
    data_loaded = yaml.safe_load(stream)
    for name in data_loaded:
        if data_loaded[name]["world"] == "world":
            data_loaded[name]["locationY"] = data_loaded[name]["locationY"] - 64
            data_loaded[name]["minCornerY"] = data_loaded[name]["minCornerY"] - 64
            data_loaded[name]["maxCornerY"] = data_loaded[name]["maxCornerY"] - 64
        if "target" in data_loaded[name]:
            if data_loaded[name]["target"]["world"] == "world":
                data_loaded[name]["target"]["y"] = data_loaded[name]["target"]["y"] - 64
with open("PluginData.yml", 'w') as yml_file:
    yml_file.write(yaml.dump(data_loaded, sort_keys=False))