import { Button, Form, Input } from "antd";
import { FormComponentProps } from "antd/lib/form";
import React from "react";

class #(name)Form extends React.Component<FormComponentProps & {
    #(firstCharToLowerCase(name)): #(name)
}> {

  handleSubmit = (e: React.FormEvent<any>) => {
    e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        
      }
    });
  };

  render() {
    const { #(firstCharToLowerCase(name)) } = this.props;
    const { getFieldDecorator } = this.props.form;

    const formItemLayout = {
      labelCol: {
        xs: { span: 24 }
      },
      wrapperCol: {
        xs: { span: 24 }
      }
    };
    const tailFormItemLayout = {
      wrapperCol: {
        xs: {
          span: 24,
          offset: 0
        }
      }
    };

    return (
      <Form onSubmit={this.handleSubmit}>
       #for(col:columns)
        <Form.Item {...formItemLayout} label="#(col.remarks)">
          {getFieldDecorator("#(col.name)", {
            initialValue: #(firstCharToLowerCase(name)).#(col.name),
            rules: []
          })(<Input />)}
        </Form.Item>
       #end
        <Form.Item {...tailFormItemLayout}>
          <Button type="primary" htmlType="submit">
            确定
          </Button>
        </Form.Item>
      </Form>
    );
  }
}

export default Form.create()(#(name)Form);
