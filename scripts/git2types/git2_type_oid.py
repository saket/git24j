import re

from .git2_types import Git2Type


class Git2TypeConstOid(Git2Type):
    """
        const git_oid *oid:
        - (header param): 'jobject oid'
        - (wrapper_before): ' git_oid c_oid; \n j_git_oid_from_java(env, oid, &c_oid);'
        - (c function param): '&c_oid'
        - (wrapper_after): ""
        - (jni param): Oid oid
        """
    PAT = re.compile(
        r"^const\s+git_oid\s+(?P<is_pointer>\*)(?P<var_name>\w+)$")
    C_HEADER_PARAM_STR = "jobject {jni_var_name}"
    C_WRAPPER_BEFORE_STR = "\t git_oid c_{var_name}; \n\t j_git_oid_from_java(env, {jni_var_name}, &c_{var_name});\n"
    C_PARAM_STR = "&c_{var_name}"
    C_WRAPPER_AFTER_STR = ""
    JNI_PARAM_STR = "Oid {jni_var_name}"


class Git2TypeOid(Git2Type):
    """
        git_oid *oid:
        - (header param): 'jobject oid'
        - (wrapper_before): ' git_oid c_oid;'
        - (c function param): "&c_oid"
        - (wrapper_after):  ' j_git_oid_to_java(env, &c_oid, oid);'
        - (jni param): Oid oid
        """
    PAT = re.compile(r"^git_oid\s+(?P<is_pointer>\*)(?P<var_name>\w+)$")
    C_HEADER_PARAM_STR = "jobject {var_name}"
    C_WRAPPER_BEFORE_STR = "\t git_oid c_{var_name};\n"
    C_PARAM_STR = "&c_{var_name}"
    C_WRAPPER_AFTER_STR = "\t j_git_oid_to_java(env, &c_{var_name}, {var_name});\n"
    JNI_PARAM_STR = "Oid {var_name}"
