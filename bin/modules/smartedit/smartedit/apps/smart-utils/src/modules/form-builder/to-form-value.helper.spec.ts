/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { FormGroupSchema } from './models';
import { toFormValue } from './to-form-value.helper';

const data = {
    input1: 'i1',
    input2: 'i2',
    input3: 'i3',
    input4: 'i4',
    input5: 'i5',
    columns: {
        input6: 'i6',
        input7: 'i7'
    },
    tags: '',
    input: 1,
    pending: 1,
    first_name: {
        en: 'en1',
        fr: null,
        ru: null
    },
    last_name: {
        en: 'en2',
        fr: null,
        ru: null
    },
    shoppingList: ['meat', 'bread', 'eggs', 'cereal', 'milk'],
    pizzaAndIngredients: [
        'Mammamia Pizza',
        {
            name: 'Pizzzaria',
            price: 2.5
        },
        {
            name: 'Tomatoooe',
            price: 3.5
        },
        {
            name: 'Peproni',
            price: 4.5
        }
    ]
};

const schema: FormGroupSchema = {
    type: 'group',
    schemas: {
        tabs: {
            persist: false,
            type: 'group',
            component: 'tabs',
            inputs: {
                label: 'TABING EXAMPLE',
                active: 'tab1'
            },
            schemas: {
                tab1: {
                    persist: false,
                    type: 'group',
                    schemas: {
                        input1: {
                            type: 'field',
                            component: 'text',
                            inputs: {
                                label: 'Field 1 in TAB 1'
                            }
                        }
                    },
                    inputs: {
                        label: 'TAB1'
                    }
                },
                tab2: {
                    persist: false,
                    type: 'group',
                    inputs: {
                        label: 'TAB2'
                    },
                    schemas: {
                        input2: {
                            type: 'field',
                            component: 'text',
                            inputs: {
                                label: 'Field 1 in TAB 2'
                            }
                        },
                        input3: {
                            type: 'field',
                            component: 'text',
                            validators: {
                                required: true
                            },
                            inputs: {
                                label: 'Field 2 in TAB 2'
                            }
                        },
                        input4: {
                            type: 'field',
                            component: 'text',
                            inputs: {
                                label: 'Field 3 in TAB 2'
                            }
                        }
                    }
                },
                input5: {
                    type: 'field',
                    component: 'text',
                    inputs: {
                        label: 'Field 1 in TAB 3'
                    }
                }
            }
        },
        columns: {
            inputs: {
                label: 'COLUMN EXAMPLE'
            },
            type: 'group',
            component: 'cols',
            schemas: {
                col1: {
                    persist: false,
                    type: 'group',
                    inputs: {
                        label: 'COLUMN1'
                    },
                    schemas: {
                        col1: {
                            persist: false,
                            type: 'group',
                            inputs: {
                                label: 'COLUMN1'
                            },
                            schemas: {
                                input6: {
                                    type: 'field',
                                    component: 'text',
                                    inputs: {
                                        label: 'Field 1 in COL 1'
                                    }
                                }
                            }
                        },
                        input7: {
                            type: 'field',
                            component: 'text',
                            inputs: {
                                label: 'Field 2 in COL 1'
                            },
                            validators: {
                                required: true
                            }
                        }
                    }
                },
                col2: {
                    persist: false,
                    type: 'group',
                    inputs: {
                        label: 'COLUMN2'
                    },
                    schemas: {
                        input8: {
                            type: 'field',
                            component: 'text',
                            inputs: {
                                label: 'Field 2 in COL 2'
                            }
                        }
                    }
                }
            }
        },
        custom: {
            type: 'group',
            component: 'custom',
            persist: false,
            schemas: {
                tags: {
                    type: 'field',
                    component: 'text',
                    inputs: {
                        label: 'TAGS'
                    }
                },
                input: {
                    type: 'field',
                    component: 'text',
                    inputs: {
                        label: 'INPUT'
                    }
                },
                pending: {
                    type: 'field',
                    component: 'text',
                    inputs: {
                        label: 'PENDING'
                    }
                }
            }
        },
        shoppingList: {
            type: 'list',
            component: 'list',
            schema: {
                type: 'field',
                component: 'text',
                inputs: {
                    label: "It's aaaaaa shoppu!!."
                }
            }
        },
        pizzaAndIngredients: {
            type: 'list',
            component: 'list',
            schema: [
                {
                    type: 'field',
                    component: 'text',
                    inputs: {
                        label: "It's aaaaaa pizzza!!."
                    }
                },
                {
                    type: 'group',
                    schemas: {
                        name: {
                            type: 'field',
                            component: 'text',
                            inputs: {
                                label: "It's aaaaaa name!."
                            }
                        },
                        price: {
                            type: 'field',
                            component: 'text',
                            inputs: {
                                label: "It's aaaaaa price!."
                            }
                        }
                    }
                }
            ]
        },
        first_name: {
            type: 'group',
            component: 'translation',
            schemas: {
                en: {
                    type: 'field',
                    component: 'text',
                    inputs: {
                        label: 'ENGLISH TEXT'
                    }
                },
                fr: {
                    type: 'field',
                    component: 'text',
                    inputs: {
                        label: 'FRENCH TEXT'
                    }
                },
                ru: {
                    type: 'field',
                    component: 'text',
                    inputs: {
                        label: 'RUSSIAN TEXT'
                    },
                    validators: {
                        required: true
                    }
                }
            },
            inputs: {
                label: 'First Name'
            }
        },
        last_name: {
            type: 'group',
            component: 'translation',
            schemas: {
                en: {
                    type: 'field',
                    component: 'text',
                    inputs: {
                        label: 'ENGLISH TEXT'
                    }
                },
                fr: {
                    type: 'field',
                    component: 'text',
                    inputs: {
                        label: 'FRENCH TEXT'
                    }
                },
                ru: {
                    type: 'field',
                    component: 'text',
                    inputs: {
                        label: 'RUSSIAN TEXT'
                    },
                    validators: {
                        required: true
                    }
                }
            },
            inputs: {
                label: 'Last Name'
            }
        }
    }
};

const value = {
    tabs: {
        tab1: {
            input1: 'i1'
        },
        tab2: {
            input2: 'i2',
            input3: 'i3',
            input4: 'i4'
        },
        input5: 'i5'
    },
    columns: {
        col1: {
            col1: {
                input6: 'i6'
            },
            input7: 'i7'
        },
        col2: {
            input8: null
        }
    },
    custom: {
        tags: '',
        input: 1,
        pending: 1
    },
    shoppingList: ['meat', 'bread', 'eggs', 'cereal', 'milk'],
    pizzaAndIngredients: [
        'Mammamia Pizza',
        {
            name: 'Pizzzaria',
            price: 2.5
        },
        {
            name: 'Tomatoooe',
            price: 3.5
        },
        {
            name: 'Peproni',
            price: 4.5
        }
    ],
    first_name: {
        en: 'en1',
        fr: null,
        ru: null
    },
    last_name: {
        en: 'en2',
        fr: null,
        ru: null
    }
};

const nullValue = {
    tabs: {
        tab1: {
            input1: null
        },
        tab2: {
            input2: null,
            input3: null,
            input4: null
        },
        input5: null
    },
    columns: {
        col1: {
            col1: {
                input6: null
            },
            input7: null
        },
        col2: {
            input8: null
        }
    },
    custom: {
        tags: null,
        input: null,
        pending: null
    },
    shoppingList: [],
    pizzaAndIngredients: [],
    first_name: {
        en: null,
        fr: null,
        ru: null
    },
    last_name: {
        en: null,
        fr: null,
        ru: null
    }
};

describe('toFormValue', () => {
    it('should generate form value correctly', () => {
        const generatedValue = toFormValue(data, schema);
        expect(generatedValue).toEqual(value);
    });

    it('should generate form value with null values', () => {
        const generatedValue = toFormValue({}, schema);
        expect(generatedValue).toEqual(nullValue);
    });

    it('should handle when data is null', () => {
        const generatedValue = toFormValue(null, schema);
        expect(generatedValue).toEqual(nullValue);
    });
});
